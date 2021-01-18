package emfcodegenerator

class TestMain {
	def static void main(String[] args){
		var ecore = "D:\\Desktop\\emf\\CodeGenPlayground\\model\\codeGenPlayground.ecore"
		var genmodel = "D:\\Desktop\\emf\\CodeGenPlayground\\model\\codeGenPlayground.genmodel"
		
		//var ecore = "D:\\Desktop\\emf\\seperatists\\model\\seperatists.ecore"
		//var genmodel = "D:\\Desktop\\emf\\seperatists\\model\\seperatists.genmodel"
		
		var generator = new EMFCodeGenerator(ecore, genmodel)
		generator.generate_interfaces()
		generator.generate_implementation()
		generator.generate_package_interfaces()
		generator.generate_package_implementations()
		generator.generate_package_factory_interfaces()
		generator.generate_package_factory_implementations()
		println("EOF")
	}
}