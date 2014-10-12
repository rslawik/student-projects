import unittest, os, subprocess, sys, threading, time, types
import master

class TestTask(unittest.TestCase):
	def setUp(self):
		self.masterThread = threading.Thread(target=master.main)
		self.masterThread.start()
		time.sleep(1)
		self.slaveThread = threading.Thread(target=self.startSlave)
		self.slaveThread.start()
		time.sleep(3)
	def testFastTask(self):
		"""Test sprawdzajacy czy pojedyncze zadanie jest wykonywane poprawnie"""
		master.taskManager.load_task("fasttask.pyt")
		self.wait()
		self.assertEqual(master.output.getvalue(), "FAST TASK\n123\n")
		self.assertEqual(master.task, None)
		self.assertEqual(master.taskManager.results, {})
	def testKillSlave(self):
		"""Test sprawdzajacy czy po zwolnieniu Slave'a, ktory wykonywal jakies zadanie, to zadanie zostanie przez pozostalych w calosci wykonane"""
		slaveThread2 = threading.Thread(target=self.startSlave)
		slaveThread2.start()
		master.taskManager.load_task("task1.pyt")
		time.sleep(10)
		master.slaveList[1][2].set_free()
		self.wait()
		self.assertEqual(master.output.getvalue(), "sample task\nRESULT1: 3\nRESULT2: 5\n")
		self.assertEqual(master.task, None)
		self.assertEqual(master.taskManager.results, {})
	def testPrime(self):
		"""Test sprawdzajacy czy wiele zadan jest liczonych poprawnie"""
		slaveThread2 = threading.Thread(target=self.startSlave)
		slaveThread2.start()
		master.taskManager.load_task("prime.pyt")
		self.wait()
		self.assertEqual(master.output.getvalue(), "102433454123133171 is not prime\n")
		self.assertEqual(master.task, None)
		self.assertEqual(master.taskManager.results, {})
	def tearDown(self):
		master.window.destroy()
		self.masterThread.join()
		self.slaveThread.join()
	def wait(self):
		while master.task is not None:
			time.sleep(1)
	def startSlave(self):
		subprocess.call("python2 " + os.path.join(os.path.dirname(sys.argv[0]), "slave.py"), shell = True)
    
if __name__ == "__main__":
	unittest.main()