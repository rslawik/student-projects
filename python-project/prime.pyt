#bigint = 6783642378468129649827649287649827468912469201
#bigint =  67836423784681296498
#bigint = 81
bigint = 102433454123133171

def divide(num, a, b):
	k = a
	while k < b:
	#for k in range(a, b):
		#print k
		if num % k == 0:
			return True
		k += 1
	return False

taskList = []
lr = 2
b = int(bigint**0.5)
r = 2
step = 10000000
while r < b:
#for r in xrange(lr, b):
	#print "divide", lr, r
	taskList.append(add_task(divide, (bigint, lr, r)))
	lr = r
	r += step
taskList.append(add_task(divide, (bigint, lr, b+1)))

res = map(get_task, taskList)

#print res
p = True
for r in res:
	if r: p = False; break
if p:
	print bigint, "is prime"
else:
	print bigint, "is not prime"
