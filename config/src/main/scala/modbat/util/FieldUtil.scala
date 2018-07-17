package modbat.util

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/** Helper class to deal with field accesses via reflection */
object FieldUtil {
// check against field names needed because isAssignableFrom
// does not seem to work properly under JPF
  def isBoolean(f: Field): Boolean = isBoolean(f.getType())

  def isBoolean(c: Class[_]) =
    (c.isAssignableFrom(classOf[Boolean]) ||
     c.getName().equals("boolean"))

  def isByte(f: Field): Boolean = isByte(f.getType())

  def isByte(c: Class[_]) =
    (c.isAssignableFrom(classOf[Byte]) ||
     c.getName().equals("byte"))

  def isChar(f: Field): Boolean = isChar(f.getType())

  def isChar(c: Class[_]) =
    (c.isAssignableFrom(classOf[Char]) ||
     c.getName().equals("char"))

  def isShort(f: Field): Boolean = isShort(f.getType())

  def isShort(c: Class[_]) =
    (c.isAssignableFrom(classOf[Short]) ||
     c.getName().equals("short"))

  def isInt(f: Field): Boolean = isInt(f.getType())

  def isInt(c: Class[_]) =
    (c.isAssignableFrom(classOf[Int]) ||
     c.getName().equals("int"))

  def isLong(f: Field): Boolean = isLong(f.getType())

  def isLong(c: Class[_]) =
    (c.isAssignableFrom(classOf[Long]) ||
     c.getName().equals("long"))

  def isFloat(f: Field): Boolean = isFloat(f.getType())

  def isFloat(c: Class[_]) =
    (c.isAssignableFrom(classOf[Float]) ||
     c.getName().equals("float"))

  def isDouble(f: Field): Boolean = isDouble(f.getType())

  def isDouble(c: Class[_]) =
    (c.isAssignableFrom(classOf[Double]) ||
     c.getName().equals("double"))

  def clearPrivate(f: Field) {
    if (Modifier.isPrivate(f.getModifiers())) {
      f.setAccessible(true)
    }
  }

  def setInt(f: Field, i: Object, v: Int) {
    val fName = f.getName
    try {
      val setter = i.getClass.getDeclaredMethod(fName + "_$eq", Integer.TYPE)
      setter.invoke(i, v.asInstanceOf[Integer]);
    } catch {
      case e: NoSuchMethodException => clearPrivate(f); f.setInt(i, v)
    }
  }

  def setLong(f: Field, i: Object, v: Long) {
    val fName = f.getName
    try {
      val setter =
	i.getClass.getDeclaredMethod(fName + "_$eq", java.lang.Long.TYPE)
      setter.invoke(i, v.asInstanceOf[java.lang.Long]);
    } catch {
      case e: NoSuchMethodException => clearPrivate(f); f.setLong(i, v)
    }
  }

  def setBoolean(f: Field, i: Object, v: Boolean) {
    val fName = f.getName
    try {
      val setter =
	i.getClass.getDeclaredMethod(fName + "_$eq", java.lang.Boolean.TYPE)
      setter.invoke(i, v.asInstanceOf[java.lang.Boolean]);
    } catch {
      case e: NoSuchMethodException => clearPrivate(f); f.setBoolean(i, v)
    }
  }

  def setDouble(f: Field, i: Object, v: Double) {
    val fName = f.getName
    try {
      val setter =
	i.getClass.getDeclaredMethod(fName + "_$eq", java.lang.Double.TYPE)
      setter.invoke(i, v.asInstanceOf[java.lang.Double]);
    } catch {
      case e: NoSuchMethodException => clearPrivate(f); f.setDouble(i, v)
    }
  }

  def set(f: Field, i: Object, v: Object) {
    val fName = f.getName
    try {
      val setter = i.getClass.getDeclaredMethod(fName + "_$eq", classOf[Object])
      setter.invoke(i, v);
    } catch {
      case e: NoSuchMethodException => clearPrivate(f); f.set(i, v)
    }
  }

  def getByte(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Byte]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getByte(i).asInstanceOf[Byte]
      }
    }
  }

  def getChar(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Char]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getChar(i).asInstanceOf[Char]
      }
    }
  }

  def getShort(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Short]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getShort(i).asInstanceOf[Short]
      }
    }
  }

  def getInt(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Int]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getInt(i).asInstanceOf[Int]
      }
    }
  }

  def getLong(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Long]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getLong(i).asInstanceOf[Long]
      }
    }
  }

  def getBoolean(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Boolean]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getBoolean(i).asInstanceOf[Boolean]
      }
    }
  }

  def getFloat(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Float]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getFloat(i).asInstanceOf[Float]
      }
    }
  }

  def getDouble(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i).asInstanceOf[Double]
    } catch {
      case e: NoSuchMethodException => {
	clearPrivate(f)
	f.getDouble(i).asInstanceOf[Double]
      }
    }
  }

  def get(f: Field, i: Object) = {
    try {
      i.getClass.getDeclaredMethod(f.getName).invoke(i)
    } catch {
      case e: NoSuchMethodException => clearPrivate(f); f.get(i)
    }
  }

  def getValue(f: Field, i: Object) = {
    if (isBoolean(f)) getBoolean(f, i)
    else if (isByte(f)) getByte(f, i)
    else if (isChar(f)) getChar(f, i)
    else if (isShort(f)) getShort(f, i)
    else if (isInt(f)) getInt(f, i)
    else if (isLong(f)) getLong(f, i)
    else if (isFloat(f)) getFloat(f, i)
    else if (isDouble(f)) getDouble(f, i)
    else get(f, i)
  }
}
