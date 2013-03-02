/**
 *  Copyright 2013 Gabriel Casarini
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package muki.tool;

/**
 * Instances of this class are used to collect the information of the generation process through its
 * various stages. An ExecutionResult has a buffer where the client appends messages and also a flag
 * to indicate the result of the process.
 */
public class ExecutionResult {

	private StringBuffer buffer;
	private boolean ok;
	
	public ExecutionResult() {
		this.setBuffer(new StringBuffer());
		this.setOk(true);
	}
	
	public void append(String message) {
		this.getBuffer().append("\n").append(message);
	}
	
	public String getLog() {
		return this.getBuffer().toString();
	}
	
	private StringBuffer getBuffer() {
		return buffer;
	}
	
	private void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}
	
	public boolean isOk() {
		return ok;
	}
	
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	
}
