print "FAST TASK"
def fun(a):
	import time
	time.sleep(5)
	return a

taskid = add_task(fun, (123,))
print get_task(taskid)