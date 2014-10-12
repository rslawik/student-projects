def test(a, b):
	import time
	time.sleep(30)
	return a+b
print "sample task"
taskid1 = add_task(test, (1,2))
taskid2 = add_task(test, (2,3))
#print "TASKID:", taskid1, taskid2
result = get_task(taskid1)
print "RESULT1:", result
result = get_task(taskid2)
print "RESULT2:", result
