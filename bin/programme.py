import csv

c = csv.writer(open("outputs.csv", "w"))
class TestCase:
    Classpath = None
    App = None
    Param = []

currentCase = TestCase()
currentCase.Classpath=None
currentCase.App=None
currentCase.Param=[]
i=1
c.writerow(['TestCase', 'CLASSPATH', 'appString', 'Param1', 'Param2', 'Param3', 'Param4', 'Param5', 'Param6', 'Param7'])

with open("/Users/alexandrefournel/Desktop/Cours/2A/PRE/PRE_STAGE/DeMonCote/terminal.txt") as f:
  content = f.readlines() 
  for word in content :
    if '>' in word:
      pass
    elif word=='\n':
      if len(currentCase.Param)==1:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], '', '', '', '', '', ''])
      elif len(currentCase.Param)==2:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], currentCase.Param[1], '', '', '', '', ''])
      elif len(currentCase.Param)==3:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], currentCase.Param[1], currentCase.Param[2], '', '', '', ''])
      elif len(currentCase.Param)==4:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], currentCase.Param[1], currentCase.Param[2], currentCase.Param[3], '', '', ''])
      elif len(currentCase.Param)==5:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], currentCase.Param[1], currentCase.Param[2], currentCase.Param[3], currentCase.Param[4], '', ''])
      elif len(currentCase.Param)==6:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], currentCase.Param[1], currentCase.Param[2], currentCase.Param[3], currentCase.Param[4], currentCase.Param[5], ''])
      elif len(currentCase.Param)==7:
        c.writerow([i,currentCase.Classpath, currentCase.App, currentCase.Param[0], currentCase.Param[1], currentCase.Param[2], currentCase.Param[3], currentCase.Param[4], currentCase.Param[5], currentCase.Param[6]])
      i+=1


      currentCase.Classpath=None
      currentCase.App=None
      currentCase.Param=[]
    else:
      if word.startswith('CLASSPATH') :
        currentCase.Classpath=word[:-2]
        line=1
      elif word.startswith('scala ') :
        currentCase.App=word[:-3]
      elif word.startswith('build') :
        currentCase.App=currentCase.App+" "+word[:-2]
      else:
        currentCase.Param=currentCase.Param+[word[:-2]]
      




