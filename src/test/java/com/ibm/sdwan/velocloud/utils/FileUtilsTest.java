package com.ibm.sdwan.velocloud.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

	@Test
	@DisplayName("Testing positive scenario for LifecycleScripts for filename and entry are same")
	public void getFileFromLifecycleScriptsTest() {
		FileUtils.getFileFromLifecycleScripts("UEsDBBQACAgIAAAAIQAAAAAAAAAAAAAAAAAZABEAdGVtcGxhdGVzL0RlbGV0ZUVkZ2UuanNvblVUDQAHAAAAAAAAAAAAAAAAq+ZSUFBKzStJLSooyixO9UxRslKorlZAFlGordUBqcqEymWCRLhqAVBLBwhH2pcgKAAAADoAAABQSwMEFAAICAgAAAAhAAAAAAAAAAAAAAAAABwAEQB0ZW1wbGF0ZXMvRWRnZVByb3Zpc2lvbi5qc29uVVQNAAcAAAAAAAAAAAAAAABdzkEKwyAQheF9TiGue4LuWyiU3GESX4OgY1HTUoJ3jxYNtkv/+ZhxG4SQ4Aj/9DrgpuRZbJvoi0jpVNTs+KGX1VPUjhv8i82SgY/hwjQZFCkz/WkZyq+0TsGMq53gq+vKoaAW3PUM7r7Yp3ZX4ZXTlaw2n7quT8c+Jos6D+pNPOZ3GQ5pB1BLBwhYrn36hgAAABEBAABQSwECFAAUAAgICAAAACEAR9qXICgAAAA6AAAAGQAJAAAAAAAAAAAAAAAAAAAAdGVtcGxhdGVzL0RlbGV0ZUVkZ2UuanNvblVUBQAHAAAAAFBLAQIUABQACAgIAAAAIQBYrn36hgAAABEBAAAcAAkAAAAAAAAAAAAAAIAAAAB0ZW1wbGF0ZXMvRWRnZVByb3Zpc2lvbi5qc29uVVQFAAcAAAAAUEsFBgAAAAACAAIAowAAAGEBAAAAAA==", 
				 "templates/DeleteEdge.json");
	}
	
	@Test
	@DisplayName("Testing negative scenario for LifecycleScripts for filename and entry are not same")
	public void getFileFromLifecycleScriptsTest1() {
		FileUtils.getFileFromLifecycleScripts("UEsDBBQACAgIAAAAIQAAAAAAAAAAAAAAAAAZABEAdGVtcGxhdGVzL0RlbGV0ZUVkZ2UuanNvblVUDQAHAAAAAAAAAAAAAAAAq+ZSUFBKzStJLSooyixO9UxRslKorlZAFlGordUBqcqEymWCRLhqAVBLBwhH2pcgKAAAADoAAABQSwMEFAAICAgAAAAhAAAAAAAAAAAAAAAAABwAEQB0ZW1wbGF0ZXMvRWRnZVByb3Zpc2lvbi5qc29uVVQNAAcAAAAAAAAAAAAAAABdzkEKwyAQheF9TiGue4LuWyiU3GESX4OgY1HTUoJ3jxYNtkv/+ZhxG4SQ4Aj/9DrgpuRZbJvoi0jpVNTs+KGX1VPUjhv8i82SgY/hwjQZFCkz/WkZyq+0TsGMq53gq+vKoaAW3PUM7r7Yp3ZX4ZXTlaw2n7quT8c+Jos6D+pNPOZ3GQ5pB1BLBwhYrn36hgAAABEBAABQSwECFAAUAAgICAAAACEAR9qXICgAAAA6AAAAGQAJAAAAAAAAAAAAAAAAAAAAdGVtcGxhdGVzL0RlbGV0ZUVkZ2UuanNvblVUBQAHAAAAAFBLAQIUABQACAgIAAAAIQBYrn36hgAAABEBAAAcAAkAAAAAAAAAAAAAAIAAAAB0ZW1wbGF0ZXMvRWRnZVByb3Zpc2lvbi5qc29uVVQFAAcAAAAAUEsFBgAAAAACAAIAowAAAGEBAAAAAA==", 
				 "templates/test.json");
	}

}