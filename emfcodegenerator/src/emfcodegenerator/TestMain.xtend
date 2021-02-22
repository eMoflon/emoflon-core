package emfcodegenerator

import java.io.File

/**
 * TODO: move to EMFCodeGenerator
 */
class TestMain {
	def static void main(String[] args){
		val pathSeparator = if (System.getProperty("os.name").startsWith("Windows")) {
			'''\'''
		} else {
			'''/'''
		}
		
		//look for models in current directory if none specified
		val modelPath = if (args.length > 0) args.get(0) else "."
		val ecore = modelPath + pathSeparator + if (args.length > 1) args.get(1) else findFile(modelPath, pathSeparator, "ecore");
		//if no filename is specified, assume the name is the same as for the ecore
		val genmodel = modelPath + pathSeparator + if (args.length > 2) args.get(2) else ecore.replace(".ecore", ".genmodel");
		
		val generator = new EMFCodeGenerator(ecore, genmodel)
		generator.generate_interfaces()
		generator.generate_implementation()
		generator.generate_package_interfaces()
		generator.generate_package_implementations()
		generator.generate_package_factory_interfaces()
		generator.generate_package_factory_implementations()
		println("EOF")
	}
		
	/**
	 * Finds the first file in the directory with the specified extension.
	 */
	def static findFile(String directory, String separator, String fileExt) {
		return directory + separator + new File(directory).list().filter[it.matches(".*\\." + fileExt)].iterator().next()
	}
		
}