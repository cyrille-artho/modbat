import csv

reader = csv.reader(open("List_class_files_without_suffix.csv","r"))
writer = csv.writer(open("List_class_files_without_suffix_and_redundancy.csv", "w"))

lastnames=[]
for row in reader: 
    if row[0] not in lastnames: 
        writer.writerow(row) 
        lastnames+=[row[0]] 