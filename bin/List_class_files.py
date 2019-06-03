import csv

cr = csv.reader(open("outputs.csv","r"))
c = csv.writer(open("List_class_files.csv", "w"))


arguments = []
i=0
for row in cr: 
    if i>0:
        arguments = row[1:]
        for item in arguments:
            if item.startswith('modbat'):
                c.writerow([item])
    i+=1


