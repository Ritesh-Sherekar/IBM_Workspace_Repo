package convertion1;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class Java_CN_2_Flow_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbOutputTerminal out = getOutputTerminal("out");
        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;

        try {
            // Log the input message
            System.out.println("Input message: " + inMessage.toString());

            // Create a new empty message for the output
            MbMessage outMessage = new MbMessage();
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
            
            // Getting the root element from the input assembly (JSON)
            MbElement InputRoot = inMessage.getRootElement();

            // Fetching the ID and Name from the JSON message
            MbElement id = InputRoot.getFirstElementByPath("JSON/Data/student/ID");
            MbElement name = InputRoot.getFirstElementByPath("JSON/Data/student/Name");

            // Check if id or name is null and throw an exception if so
            if (id == null || name == null) {
                throw new MbUserException(this, "evaluate()", "", "", "ID or Name not found in input JSON", null);
            }

            // Create the XML output
            MbElement OutputRoot = outMessage.getRootElement();
            MbElement studentElement = OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "student", null);
            studentElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "id", id.getValue().toString());
            studentElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "name", name.getValue().toString());

        } catch (MbException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        }

        // Propagate the transformed message to the 'out' terminal
        out.propagate(outAssembly);
    }
}
