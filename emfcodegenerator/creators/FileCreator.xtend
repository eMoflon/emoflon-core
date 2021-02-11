package emfcodegenerator.creators

/**
 * Common methods for all classes which generate a file.
 * @author Adrian Zwenger
 */
interface FileCreator {
	
	/**
	 * Initializes the creator and gathers all needed information for code generation.
	 * @param fq_file_path String fully qualified path and name of the file which shall be written
	 * @param IDENTION String with which the code shall be idented
	 */
	def void initialize_creator(String fq_file_path, String IDENTION)
	
	/**
	 * Starts the writing process. The file is created.
	 */
	def void write_to_file()
}