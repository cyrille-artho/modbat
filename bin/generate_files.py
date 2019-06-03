import csv
import re

reader = csv.reader(open("List_class_files_without_suffix_and_redundancy.csv","r"))


list_class_files=[]
for row_reader in reader: 
    list_class_files+=[row_reader[0]]

for item in list_class_files:
    index=1
    item_suffix=re.split(r"\.",item)[-1:][0]
    item_mime_type=item_suffix+".scala"
    fichier = open("../src/test/scala/modbat/mbt/"+item_mime_type, "w")
    fichier.write("package modbat.mbt")
    fichier.write("\n")
    fichier.write("\n")
    fichier.write("import org.scalatest._")
    fichier.write("\n")
    fichier.write("\n") 
    fichier.write("class ")
    fichier.write(item_suffix)
    fichier.write(" extends FlatSpec with Matchers {")
    i=0
    list_arguments = []
    cr = csv.reader(open("outputs.csv","r"))
    for row_cr in cr: 
        if (i>0):
            k=0
            for row_item in row_cr[1:]:
                if item in row_item:
                    k=1
            if (k==1):
                list_arguments += [row_cr[1:]]
        else:
            i=1
    for different_arguments in list_arguments:
        fichier.write("\n")
        fichier.write("  ")
        fichier.write("\"")
        fichier.write(item_suffix)
        fichier.write(str(index))
        index+=1
        fichier.write("\"")
        fichier.write(" should \"pass\" in {")
        fichier.write("\n")
        fichier.write("  ")
        fichier.write("  ")
        fichier.write("val result = ModbatTestHarness.testMain(Array(")
        j=0
        classpath_variable=0
        scala_variable=0
        for parameter in different_arguments :
            if parameter.startswith('CLASSPATH'):
                if parameter.startswith('CLASSPATH=build/modbat-examples.jar'):
                    classpath_variable=1
                elif parameter.startswith('CLASSPATH=build/modbat-test.jar'):
                    classpath_variable=2
            elif parameter.startswith('scala build'):
                if classpath_variable==0:
                    scala_variable=1
            elif parameter=="":
                pass
            else :
                if j==1:
                    fichier.write(",")
                else:
                    j=1
                fichier.write("\"")
                while parameter[-1:] in [' ']:
                    parameter=parameter[0:-1]
                fichier.write(parameter)
                fichier.write("\"")
        if classpath_variable==1:
            fichier.write("), ModbatTestHarness.setExamplesJar)")
        elif classpath_variable==2:
            fichier.write("), ModbatTestHarness.setTestJar)")
        elif scala_variable==1:
            fichier.write("), ModbatTestHarness.setFooJar)")
        else:
            print("Same Error")
        fichier.write("\n")
        fichier.write("  ")
        fichier.write("  ")
        fichier.write("result._1 should be(0)")
        fichier.write("\n")
        fichier.write("  ")
        fichier.write("  ")
        fichier.write("result._3 shouldBe empty")
        fichier.write("\n")
        fichier.write("  ") 
        fichier.write("}")
        fichier.write("\n")
        fichier.write("\n")
        fichier.write("\n")

    fichier.write("\n") 
    fichier.write("}")
    fichier.close()
