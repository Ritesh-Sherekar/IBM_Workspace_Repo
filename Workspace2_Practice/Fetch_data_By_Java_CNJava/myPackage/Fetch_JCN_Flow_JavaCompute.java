package myPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class Fetch_JCN_Flow_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbOutputTerminal out = getOutputTerminal("out");
        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;

        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;

        try {
            // Create a new output message as a copy of the input
            MbMessage outMessage = new MbMessage(inMessage);
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);

            // Get JDBC connection from the policy
            conn = getJDBCType4Connection("{jdbc}:jdbc_policy", 
                    JDBC_TransactionType.MB_TRANSACTION_AUTO);

            // Create a Statement object
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);

            // Execute the query
            resultSet = stmt.executeQuery("SELECT * FROM SYSTEM.EMP");

            // Process the result set (Example: printing results to console)
            while (resultSet.next()) {
                int empId = resultSet.getInt("EMP_ID");
                String empName = resultSet.getString("EMP_NAME");
                System.out.println("EMP_ID: " + empId + ", EMP_NAME: " + empName);
            }

        } catch (MbException e) {
            throw e; // Allow Broker handling of MbException
        } catch (Exception e) {
            // Re-throw as MbUserException for proper handling
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        } finally {
            // Close ResultSet, Statement, and Connection
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }

        // Propagate the message to the 'out' terminal
        out.propagate(outAssembly);
    }
}
