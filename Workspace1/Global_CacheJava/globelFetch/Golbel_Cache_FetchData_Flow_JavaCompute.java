package globelFetch;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

public class Golbel_Cache_FetchData_Flow_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below

			MbElement InputRoot = inMessage.getRootElement().getLastChild();
			MbElement EmpObject = InputRoot.getFirstElementByPath("Employee");
			String ID = EmpObject.getFirstElementByPath("ID").getValueAsString();
			String val = null;
			
			try {
				// Fetching value from global cache
				MbGlobalMap globalmap = MbGlobalMap.getGlobalMap();
				val =(String) globalmap.get(ID);
				
				// If value is not found, set it to "Unknown"
				if (val == null) {
					val = "Unknown";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			MbElement OutputRoot = outMessage.getRootElement().createElementAsFirstChild(MbXMLNSC.PARSER_NAME);
			MbElement ResponseObject = OutputRoot.createElementAsFirstChild(MbXMLNSC.FOLDER , "response" , "");
			ResponseObject.createElementAsFirstChild(MbXMLNSC.FIELD , "ID" , ID);
			ResponseObject.createElementAsFirstChild(MbXMLNSC.FIELD , "Name" , val);  // Name is fetched correctly here
			
			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

	@Override
	public void onPreSetupValidation() throws MbException {
	}

	@Override
	public void onSetup() throws MbException {
	}

	@Override
	public void onStart() throws MbException {
	}

	@Override
	public void onStop(boolean wait) throws MbException {
	}

	@Override
	public void onTearDown() throws MbException {
	}

}
