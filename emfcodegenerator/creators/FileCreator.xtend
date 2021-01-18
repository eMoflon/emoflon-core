package emfcodegenerator.creators

interface FileCreator {
	def void initialize_creator(String fq_file_path, String IDENTION)
	def void write_to_file()
}