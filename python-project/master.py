#!/usr/bin/env python
import gtk, marshal, pickle, Queue, SocketServer, StringIO, struct, sys, threading

Address = ("localhost", 12345)
#Address = ("192.168.1.102", 12345)
"""Namiary na Mastera"""

class MasterServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
	"""Serwer Mastera, zadania obslugiwane sa w osobnych watkach"""
	allow_reuse_address = True
	pass

class RequestHandler(SocketServer.StreamRequestHandler):
	"""Klasa obslugi pojedynczego zadania"""
	#socketLock = threading.Lock()
	#"""Blokada gniazda"""
	call = dict(
		YOUR_WISH_IS_MY_COMMAND=(
			lambda self, args: self.slaveEcho(args)),
		DONE=(
			lambda self, args: self.done(args[0])))
	"""Slownik funkcji obslugujacych komunikaty od Slave'a"""
	retry = 0
	"""Liczba nieudanych prob odczytania danych od Slave'a"""
	state = "unknown"
	"""Biezacy stan Slave'a"""
	todo = None
	"""Akutalnie wykonywana zadanie przez Slave'a"""
	free = False
	"""Zwolnienie Slave'a"""
	def handle(self):
		try:
			self.request.settimeout(5)
			self.path = slaveList.append([threading.currentThread().name, self.state, self])
			#print "Path:", self.path
			while True:
				if self.free:
					#print "free"
					break
				if self.retry == 3:
					print "Slave doesn't respond"
					break
				self.take_task()
				try:
					data = self.read()
					#print "data:", data
					self.retry = 0
					self.call[data[0]](self, data[1:])
				except Exception as err:
					print "handle.read error:", err
					self.retry += 1
					self.state = "error"
					self.updateState()
					continue
			self.write("THANKS")
			if self.todo is not None:
				taskManager.tasks.put(self.todo)
		except Exception as err:
			print "handle error:", err
		finally:
			#print "handle finally"
			slaveList.remove(self.path)
	def read(self):
		"""Odczyt z gniazda"""
		#print "read"
		sizeStruct = struct.Struct("!I")
		sizeData = self.rfile.read(sizeStruct.size)
		size = sizeStruct.unpack(sizeData)[0]
		return pickle.loads(self.rfile.read(size))
	def write(self, *items):
		"""Zapis do gniazda"""
		#print "write", items
		sizeStruct = struct.Struct("!I")
		data = pickle.dumps(items, 2)
		self.wfile.write(sizeStruct.pack(len(data)))
		self.wfile.write(data)
	def updateState(self):
		"""Uaktualnienie stanu Slave'a na liscie Slave'ow"""
		slaveList.set_value(self.path, 1, self.state)
	def slaveEcho(self, args):
		"""Reakcja na keep-avlie od Slave'a"""
		#print "keep alive"
		self.state = "ready"
		if self.todo is not None:
			self.state = "busy"
		self.updateState()
	def done(self, args):
		"""Reakcja na wynik zadania od Slave'a"""
		#print "done", args
		taskManager.results[self.todo[0]] = args
		taskManager.done.set()
		self.state = "ready"
		self.todo = None
		self.updateState()
	def take_task(self):
		"""Pobrania zadania dla Slave'a z kolejki zadan"""
		if not self.state == "ready": return
		try:
			#print "take_task"
			self.todo = taskManager.tasks.get(False)
			#print "have task"
			taskManager.tasks.task_done()
			self.state = "busy"
			self.updateState()
			#print "send task"
			self.write("DO", (marshal.dumps(self.todo[1].func_code), self.todo[2]))
		#except Exception as err: print "take_task, error:", err
		except: pass
	def set_free(self):
		"""Zwolnienie Slave'a"""
		self.free = True
		

class SlaveListModel(gtk.ListStore):
	"""Lista Slave'ow"""
	#listLock = threading.Lock()
	def __init__(self):
		super(SlaveListModel, self).__init__(str, str, object)
	#def append(self, elem):
		#with self.listLock:
		#return super(SlaveListModel, self).append(elem)

class MasterServerThread(threading.Thread):
	"""Watek, w ktorym dziala serwer Mastera"""
	def __init__(self):
		threading.Thread.__init__(self)
		self.server = None
	def run(self):
		#print "Server loop running in thread:", self.name
		try:
			self.server = MasterServer(Address, RequestHandler)
			self.server.serve_forever()
		except Exception as err:
			print "server error:", err
		finally:
			self.stop()
	def stop(self):
		"""Zatrzymanie serwera i zwolnienie wszystkich Slave'ow"""
		for slave in slaveList:
			slave[2].set_free()
		if self.server is not None:
			self.server.shutdown()

class MasterWindow(gtk.Window):
	"""Okno aplikacji"""
	def __init__(self):
		super(MasterWindow, self).__init__()
		self.set_title("Master")
		self.set_size_request(350, 300)
		self.set_position(gtk.WIN_POS_CENTER)
		self.connect("destroy", gtk.main_quit)
		self.createGUI()
		self.show_all()
	def createGUI(self):
		vbox = gtk.VBox(False, 3)
		sw = gtk.ScrolledWindow()
		sw.set_shadow_type(gtk.SHADOW_ETCHED_IN)
		sw.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
		self.treeView = gtk.TreeView(slaveList)
		self.treeView.set_rules_hint(True)
		col = gtk.TreeViewColumn("Slave Name", gtk.CellRendererText(), text=0)
		col.set_expand(True)
		self.treeView.append_column(col)
		self.treeView.append_column(gtk.TreeViewColumn("State", gtk.CellRendererText(), text=1))
		sw.add(self.treeView)
		vbox.pack_start(sw, True, True, 0)
		hbox = gtk.HBox(False, 3)
		taskb = gtk.Button("Task")
		taskb.connect("clicked", lambda x: self.task())
		hbox.pack_start(taskb, True, True, 0)
		freeb = gtk.Button("Free")
		freeb.connect("clicked", lambda x: self.free())
		hbox.pack_start(freeb, True, True, 0)
		vbox.pack_start(hbox, False, False, 0)
		self.add(vbox)
	def free(self):
		"""Zwolnienie zaznaczonego Slave'a"""
		path, col = self.treeView.get_cursor()
		#print "Free", path, col
		if path is not None:
			rh = slaveList[path][2]
			#print rh
			rh.set_free()
	def task(self):
		"""Wczytanie zadania z pliku"""
		global task
		if task is not None: return
		chooser = gtk.FileChooserDialog(title="Select task", action=gtk.FILE_CHOOSER_ACTION_OPEN,
			buttons=(gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL, gtk.STOCK_OPEN, gtk.RESPONSE_OK))
		_filter = gtk.FileFilter()
		_filter.set_name("Tasks")
		_filter.add_pattern("*.pyt")
		chooser.add_filter(_filter)
		response = chooser.run()
		if response == gtk.RESPONSE_OK:
			#print chooser.get_filename(), " selected"
			taskManager.load_task(chooser.get_filename())
		chooser.destroy()

class TaskManager():
	"""Menedzer zadan: kolejka do wykonania i slownik wynikow"""
	tasks = Queue.Queue()
	"""Kolejka zadan do wykonania"""
	results = dict()
	"""Slownik wynikow"""
	idLock = threading.Lock()
	"""Blokada przy generowaniu kolejnych identyfikatorow zadan"""
	lastid = 0
	"""Ostatnio wygenerowany identyfikator"""
	done = threading.Event()
	"""Zdarzenie pojawienia sie nowego wyniku"""
	def gen_id(self):
		"""Generowanie identyfikatora zadania"""
		with self.idLock:
			self.lastid += 1
			return self.lastid
	def add_task(self, f, args):
		"""Dodanie nowego zadania do menedzera"""
		#print "add_task", f, args
		taskid = self.gen_id()
		self.tasks.put((taskid, f, args))
		self.results[taskid] = None
		return taskid
	def get_task(self, taskid):
		"""Pobranie wyniku zadania i jego usuniecie"""
		#print "get_task", taskid
		while self.results[taskid] is None:
			self.done.wait()
		self.done.clear()
		res = self.results[taskid]
		del self.results[taskid]
		return res
	def load_task(self, filename):
		global task
		fh = open(filename)
		code = fh.read()
		#print "code:", code			
		def _task():
			global output
			output = StringIO.StringIO()
			sys.stdout = output
			"""Zadanie wykonane bedzie w osobnym watku, podpiane sa funcje dodania zadania i pobrania jego resultatu"""
			exec code in dict(add_task=taskManager.add_task, get_task=taskManager.get_task)
			# show summary ?
			sys.stdout = sys.__stdout__
			print output.getvalue()
			global task
			task = None
			#output = None
		task = threading.Thread(target=_task)
		task.start()

slaveList = SlaveListModel()
"""Lista Slave'ow"""

taskManager = TaskManager()
"""Menedzer zadan"""

task = None
"""Aktualne zadanie"""

output = None
"""Wyjscie generowane przez zadanie"""

window = None

def init():
	gtk.gdk.threads_init()
	"""Uruchomienie obslugi watkow w gtk"""
	global slaveList
	slaveList = SlaveListModel()
	global taskManager
	taskManager = TaskManager()
	global task
	task = None
	global output
	output = None

def main():
	"""Glowna funkcja programu"""
	init()
	#print "master", threading.currentThread().name
	print "I'm Master"
	serverThread = MasterServerThread()
	serverThread.start()
	global window
	window = MasterWindow()
	gtk.main()
	serverThread.stop()
	global task
	if task is not None and task.is_alive():
		task._Thread__stop()

if __name__ == "__main__":
	main()
