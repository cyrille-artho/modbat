import csv

cread = csv.reader(open("List_class_files.csv","r"))
csort = csv.writer(open("List_class_files_without_suffix.csv", "w"))

unwished_values = [" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "_"]

for row2 in cread:
    while row2[0][-1:] in unwished_values:
        row2[0]=row2[0][0:-1]
    csort.writerow(row2)