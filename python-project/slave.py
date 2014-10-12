#!/usr/bin/env python
import marshal, pickle, socket, struct, threading, types

Address = ("localhost", 12345)
"""Namiary na Mastera"""

class RepeatTimer(threading.Thread):
	"""Timer, ktory co ustaly odstep czasu wywoluje wybrana funkcje"""
	finished = threading.Event()
	"""Event zakonczenia dzialania"""
	def __init__(self, interval, function, args=(), kwargs={}):
		super(RepeatTimer, self).__init__()
		self.interval, self.function = interval, function
		self.args, self.kwargs = args, kwargs
	def run(self):
		while True:
			self.finished.wait(self.interval)
			if self.finished.is_set(): break
			self.function(*self.args, **self.kwargs)
	def cancel(self):
		"""Ustawienie zdarzenia zakonczenia"""
		self.finished.set()
		

class SocketManager:
	"""Menedzer kontekstu, ktory otwiera gniazdo sluzace do polaczenia z masterem oraz cyklicznie wysyla keep-alive"""
	sockLock = threading.Lock()
	"""Blokada gniazda, aby naraz tylko jeden watek mogl do niego pisac"""
	def __init__(self, address):
		self.address = address
	def __enter__(self):
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.sock.connect(self.address)
		self.keepAliveTimer = RepeatTimer(3, self.keep_alive)
		self.keepAliveTimer.start()
		self.keep_alive()
		return self
	def __exit__(self, *ignore):
		self.keepAliveTimer.cancel()
		self.sock.close()
	def write(self, *items):
		"""Obsluga gniazda, wysylanie"""
		try:
			with self.sockLock:
				sizeStruct = struct.Struct("!I")
				data = pickle.dumps(items, 2)
				self.sock.sendall(sizeStruct.pack(len(data)))
				self.sock.sendall(data)
		except socket.error as err:
			print "socket.write error:", err
	def read(self):
		"""Obsluga gniazda, odbieranie"""
		try:
			sizeStruct = struct.Struct("!I")
			size_data = self.sock.recv(sizeStruct.size)
			size = sizeStruct.unpack(size_data)[0]
			result = bytearray()
			while True:
				data = self.sock.recv(4000)
				if not data:
					break
				result.extend(data)
				if len(result) >= size:
					break
			return pickle.loads(result)
		except socket.error as err:
			print "socket.read error:", err
	def keep_alive(self):
		"""Keep-alive, podtrzywanie polaczenia"""
		#print "keep alive"
		self.write("YOUR_WISH_IS_MY_COMMAND")
		
worker = None
"""Watek, w ktorym wykonywanie bedzie obliczenie zadania"""

def main():
	"""Glowna funkcja programu"""
	print "I'm Slave"
	with SocketManager(Address) as sock:
		while True:
			#sock.sockLock.acquire()
			data = sock.read()
			#print data
			global worker
			if data[0] == "THANKS":
				if worker is not None:
					worker._Thread__stop()
				break
			elif data[0] == "DO":
				fun_code, args = data[1]
				fun = types.FunctionType(marshal.loads(fun_code), globals(), "function")
				def work():
					res = fun(*args)
					worker = None
					sock.write("DONE", res)
				worker = threading.Thread(target=work)
				worker.start()

if __name__ == "__main__":
	main()
