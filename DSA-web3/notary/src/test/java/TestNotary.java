import io.iconator.testonator.Contract;
import io.iconator.testonator.DeployedContract;
import io.iconator.testonator.TestBlockchain;
import io.iconator.testonator.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.iconator.testonator.TestBlockchain.CREDENTIAL_0;
import static io.iconator.testonator.TestBlockchain.compile;

public class TestNotary {

    private static TestBlockchain blockchain;
    private static Map<String, Contract> contracts;

    @BeforeClass
    public static void setup() throws Exception {
        blockchain = TestBlockchain.run();
        contracts = setup0();
    }

    @After
    public void afterTests() {
        blockchain.reset();
    }

    @Test
    public void testContract() throws InterruptedException, ExecutionException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        DeployedContract dc = blockchain.deploy(CREDENTIAL_0, contracts.get("Notary"));
        byte[] tmp = new byte[32];
        tmp[0]=1;
        tmp[31]=2;
        blockchain.call(dc, "store", tmp);

        Type t1 = blockchain.callConstant(dc, "verify", CREDENTIAL_0.getAddress(), tmp).get(0);

        //39 is the timestamp
        Assert.assertEquals("39", t1.getValue().toString());

        tmp[0]=0;
        Type t2 = blockchain.callConstant(dc, "verify", CREDENTIAL_0.getAddress(), tmp).get(0);
        Assert.assertEquals("0", t2.getValue().toString());
    }

    @Test
    public void testConnectRinkeby() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
        Web3j web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/<yourtoken>"));

        Function function = Utils.createFunction(contracts.get("Notary"), "verify", "0x25D96310cd6694d88b9c6803BE09511597C0A630", Numeric.hexStringToByteArray("0x3f3429b5b06f90c6fd93f6db3855e51b06c9b6ee8219aa1f3663a75d46618442"));
        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall ethCall = web3.ethCall(
                Transaction.createEthCallTransaction(
                        "0x25D96310cd6694d88b9c6803BE09511597C0A630", "0x2ad87157d67f7cbebfd2e2bb3d9d547f3bd8d48a", encodedFunction),
                DefaultBlockParameterName.LATEST).sendAsync().get();

        String value = ethCall.getValue();
        List<Type> list = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        System.out.println("time is: " + list.get(0).getValue().toString());
    }

    private static Map<String, Contract> setup0() throws Exception {

        File contractFile = Paths.get(ClassLoader.getSystemResource("Notary.sol").toURI()).toFile();
        Map<String, Contract> contracts = compile(contractFile);
        Assert.assertEquals(1, contracts.size());
        for(String name:contracts.keySet()) {
            System.out.println("Available contract names: " + name);
        }
        return contracts;
    }
}
