package convertion;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class Java_Compute_Node_Flow_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

        MbOutputTerminal out = getOutputTerminal("out");
        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;
        try {
            // Create a new empty message for the output
            MbMessage outMessage = new MbMessage();
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
           
            // ----------------------------------------------------------
            // Add user code below for XML to JSON transformation
           
            // Getting the root element from the input assembly (XML)
            MbElement InputRoot = inMessage.getRootElement();

            // Log to check InputRoot is not null
            System.out.println("InputRoot is: " + (InputRoot == null ? "null" : "not null"));

            if (InputRoot == null) {
                throw new MbUserException(this, "evaluate()", "", "", "InputRoot is null", null);
            }

            // Fetching the "id" and "name" from the XML message using XPath
            MbElement id = InputRoot.getFirstElementByPath("XMLNSC/student/id");
            MbElement name = InputRoot.getFirstElementByPath("XMLNSC/student/name");

            // Log to check id and name values
            System.out.println("ID element is: " + (id == null ? "null" : id.getValue()));
            System.out.println("Name element is: " + (name == null ? "null" : name.getValue()));

            // Check if id or name is null and throw an exception if so
            if (id == null || name == null) {
                throw new MbUserException(this, "evaluate()", "", "", "ID or Name not found in input XML", null);
            }

            // Clear the OutputRoot and create JSON structure
            MbElement OutputRoot = outMessage.getRootElement();

            // Log to check OutputRoot initialization
            System.out.println("OutputRoot is: " + (OutputRoot == null ? "null" : "not null"));

            if (OutputRoot == null) {
                throw new MbUserException(this, "evaluate()", "", "", "OutputRoot is null", null);
            }

            // Create the HTTP response headers to indicate JSON content-type
            MbElement httpHeaders = OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "HTTPResponseHeader", null);
            httpHeaders.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Content-Type", "application/json");

            // Manually create the structure for JSON
            MbElement jsonRoot = OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "JSON", null);
            MbElement jsonData = jsonRoot.createElementAsLastChild(MbElement.TYPE_NAME, "Data", null);
            MbElement jsonStudent = jsonData.createElementAsLastChild(MbElement.TYPE_NAME, "student", null);

            // Adding "id" and "name" fields in JSON format
            jsonStudent.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ID", id.getValue());
            jsonStudent.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", name.getValue());

            // End of user code
            // ----------------------------------------------------------

        } catch (MbException e) {
            // Re-throw to allow Broker handling of MbException
            throw e;
        } catch (RuntimeException e) {
            // Re-throw to allow Broker handling of RuntimeException
            throw e;
        } catch (Exception e) {
            // Ensure all exceptions are re-thrown to be handled in the flow
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        }

        // Propagate the transformed message to the 'out' terminal
        out.propagate(outAssembly);
    }
}
