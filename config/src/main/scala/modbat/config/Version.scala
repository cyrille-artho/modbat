package modbat.config

class Version(pkg: String) {
  val packageHandle = getClass().getClassLoader().getDefinedPackage(pkg)
  assert (packageHandle != null,
	  { "Cannot find package; available packages: " +
	    Package.getPackages.mkString("\n") })
  override def toString() = packageHandle.getSpecificationVersion +
    " rev " + packageHandle.getImplementationVersion
}
