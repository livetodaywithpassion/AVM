package org.aion.avm.core.blockchainruntime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import org.aion.avm.api.ABIDecoder;
import org.aion.avm.api.ABIEncoder;
import org.aion.avm.core.AvmImpl;
import org.aion.avm.core.CommonAvmFactory;
import org.aion.avm.core.EmptyInstrumentation;
import org.aion.avm.core.dappreading.JarBuilder;
import org.aion.avm.core.util.CodeAndArguments;
import org.aion.avm.core.util.Helpers;
import org.aion.avm.internal.IInstrumentation;
import org.aion.avm.internal.InstrumentationHelpers;
import org.aion.kernel.AvmAddress;
import org.aion.kernel.Block;
import org.aion.kernel.KernelInterfaceImpl;
import org.aion.kernel.Transaction;
import org.aion.kernel.TransactionContextImpl;
import org.aion.vm.api.interfaces.Address;
import org.aion.vm.api.interfaces.KernelInterface;
import org.aion.vm.api.interfaces.TransactionContext;
import org.aion.vm.api.interfaces.TransactionResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class InternalCallClinitAddressesTest {
    private static final int MAX_CALL_DEPTH = 10;
    private static Address from = KernelInterfaceImpl.PREMINED_ADDRESS;
    private static long energyLimit = 5_000_000L;
    private static long energyPrice = 5;
    private static Block block = new Block(new byte[32], 1, Helpers.randomAddress(), System.currentTimeMillis(), new byte[0]);

    private static KernelInterface kernel;
    private static AvmImpl avm;

    @BeforeClass
    public static void setup() {
        kernel = new KernelInterfaceImpl();
        avm = CommonAvmFactory.buildAvmInstance(kernel);
    }

    @AfterClass
    public static void tearDown() {
        avm.shutdown();
    }

    // All of the print calls become active when this is set true. By default this is false to speed
    // up the running of these tests / not pollute stdout, but a handy option for debugging.
    private static final boolean ACTIVATE_OUTPUTS_FOR_DEBUGGING = false;

    /**
     * Tests that each dApp reports the correct addresses for: origin, caller, contract
     *
     * This tests the case in which N separate deploys are deployed and there is a chain of calls
     * from one into the next through all N and each of the 3 address queries are done inside eac
     * dApp and this 'report' is returned back, parsed and verified as being correct.
     *
     * This tests the following order of events:
     *    - call other dapp first, grab its report (via recursion)
     *    - produce dapp report for self
     *
     * (ie. recursion first, self report last)
     */
    @Test
    public void testInternalCallsIntoOtherDappsDoingRecursionFirst() {
        for (int numInternalCalls = 0; numInternalCalls < MAX_CALL_DEPTH; numInternalCalls++) {
            verifyRunningInternalCallsFromDappIntoOtherDapps(numInternalCalls, true);
        }
    }

    /**
     * Tests that each dApp reports the correct addresses for: origin, caller, contract
     *
     * This tests the case in which N separate deploys are deployed and there is a chain of calls
     * from one into the next through all N and each of the 3 address queries are done inside each
     * dApp and this 'report' is returned back, parsed and verified as being correct.
     *
     * This tests the following order of events:
     *    - produce dapp report for self
     *    - call other dapp first, grab its report (via recursion)
     *
     * (ie. self report first, recursion last)
     */
    @Test
    public void testInternalCallsIntoOtherDappsDoingRecursionLast() {
        for (int numInternalCalls = 0; numInternalCalls < MAX_CALL_DEPTH; numInternalCalls++) {
            verifyRunningInternalCallsFromDappIntoOtherDapps(numInternalCalls, false);
        }
    }

    private void verifyRunningInternalCallsFromDappIntoOtherDapps(int numInternalTransactionsToSpawn, boolean recurseFirst) {
        printTestContext("verifyRunningInternalCallsFromDappIntoOtherDapps", numInternalTransactionsToSpawn, recurseFirst);

        Address contract = deployInternalCallClinitAddressTrackerContract();
        Address[] deployedContracts = generateTheAddressesOfTheContractsThatWillBeCreated(numInternalTransactionsToSpawn, contract, kernel.getNonce(contract).longValue());
        printOrderOfContractCalls(contract, deployedContracts);

        // Grab the 'report', the batch of all addresses that were tracked by the contract.
        Address[] report = callInternalCallClinitAddressesContract(contract, numInternalTransactionsToSpawn, recurseFirst);

        // Extract the origin, caller and address addresses from the report.
        Address[] originAddresses = extractOriginAddressesFromReport(report);
        Address[] callerAddresses = extractCallerAddressesFromReport(report);
        Address[] contractAddresses = extractContractAddressesFromReport(report);

        // Verify that the sizes of the batches of addresses collected is correct.
        assertEquals(numInternalTransactionsToSpawn + 1, originAddresses.length);
        assertEquals(numInternalTransactionsToSpawn + 1, callerAddresses.length);
        assertEquals(numInternalTransactionsToSpawn + 1, contractAddresses.length);

        // Every transaction should have the same origin address: the initial deployer.
        printExpectedOrderOfOriginAddresses(numInternalTransactionsToSpawn);
        printActualOrderOfOriginAddresses(originAddresses);

        for (Address origin : originAddresses) {
            assertEquals(from, origin);
        }

        // Each contract should have been called by the contract before it.
        int lengthWithLastContractDropped = Math.max(0, deployedContracts.length - 1);
        Address[] allCallersInOrder = (numInternalTransactionsToSpawn == 0)
            ? joinArrays(new Address[]{ from }, Arrays.copyOf(deployedContracts, lengthWithLastContractDropped))
            : joinArrays(new Address[]{ from, contract }, Arrays.copyOf(deployedContracts, lengthWithLastContractDropped));

        printExpectedOrderOfCallerAddresses(allCallersInOrder);
        printActualOrderOfCallerAddresses(callerAddresses);

        for (int i = 0; i < numInternalTransactionsToSpawn + 1; i++) {
            assertEquals(allCallersInOrder[i], callerAddresses[i]);
        }

        // Each contract should have reported its own address for 'address'.
        Address[] allContractsInOrderOfBeingCalled = joinArrays(new Address[]{ contract }, deployedContracts);
        printExpectedOrderOfContractAddresses(allContractsInOrderOfBeingCalled);
        printActualOrderOfContractAddresses(contractAddresses);

        for (int i = 0; i < numInternalTransactionsToSpawn + 1; i++) {
            assertEquals(allContractsInOrderOfBeingCalled[i], contractAddresses[i]);
        }
    }

    private static Address[] generateTheAddressesOfTheContractsThatWillBeCreated(int numContractsToDeploy, Address contract, long nonce) {
        Address[] contracts = new Address[numContractsToDeploy];
        for (int i = 0; i < numContractsToDeploy; i++) {
            if (i == 0) {
                contracts[i] = TransactionContextImpl.generateContractAddress(contract, nonce + i);
            } else {
                contracts[i] = TransactionContextImpl.generateContractAddress(contracts[i - 1], 0);
            }
        }
        return contracts;
    }

    private static Address deployInternalCallClinitAddressTrackerContract() {
        Transaction transaction = Transaction.create(from, kernel.getNonce(from), BigInteger.ZERO, getDappBytes(), energyLimit, energyPrice);
        TransactionContext context = new TransactionContextImpl(transaction, block);
        TransactionResult result = avm.run(new TransactionContext[] {context})[0].get();
        assertTrue(result.getResultCode().isSuccess());
        return AvmAddress.wrap(result.getReturnData());
    }

    private Address[] callInternalCallClinitAddressesContract(Address contract, int numInternalCalls, boolean recurseFirst) {
        byte[] dappBytes = getDappBytes();

        byte[] callData;
        if (recurseFirst) {
            callData = ABIEncoder.encodeMethodArguments("runInternalCallsAndTrackAddressRecurseThenGrabOwnAddress", dappBytes, numInternalCalls);
        } else {
            callData = ABIEncoder.encodeMethodArguments("runInternalCallsAndTrackAddressGrabOwnAddressThenRecurse", dappBytes, numInternalCalls);
        }

        Transaction transaction = Transaction.call(from, contract, kernel.getNonce(from), BigInteger.ZERO, callData, energyLimit, energyPrice);
        TransactionContext context = new TransactionContextImpl(transaction, block);
        TransactionResult result = avm.run(new TransactionContext[] {context})[0].get();
        assertTrue(result.getResultCode().isSuccess());
        return returnDataToAddresses(result.getReturnData());
    }

    private static byte[] getDappBytes() {
        byte[] jar = JarBuilder.buildJarForMainAndClasses(InternalCallClinitAddressesContract.class);
        return new CodeAndArguments(jar, new byte[0]).encodeToBytes();
    }

    private Address[] returnDataToAddresses(byte[] data) {
        IInstrumentation instrumentation = new EmptyInstrumentation();
        InstrumentationHelpers.attachThread(instrumentation);
        org.aion.avm.api.Address[] addresses = (org.aion.avm.api.Address[]) ABIDecoder.decodeOneObject(data);
        InstrumentationHelpers.detachThread(instrumentation);
        return convertFromAbiAddresses(addresses);
    }

    private Address[] convertFromAbiAddresses(org.aion.avm.api.Address[] addresses) {
        Address[] convertedAddresses = new Address[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            convertedAddresses[i] = AvmAddress.wrap(addresses[i].unwrap());
        }
        return convertedAddresses;
    }

    private static Address[] joinArrays(Address[] array1, Address[] array2) {
        Address[] array = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, array, array1.length, array2.length);
        return array;
    }

    /**
     * ASSUMPTION: report is an array with the following recurring pattern of addresses:
     *
     *   [ origin, caller, address ]
     *
     * AND: report.length > 0
     */
    private static Address[] extractOriginAddressesFromReport(Address[] report) {
        return extractEveryThirdAddressWithOffset(report, 0);
    }

    /**
     * ASSUMPTION: report is an array with the following recurring pattern of addresses:
     *
     *   [ origin, caller, address ]
     *
     * AND: report.length > 0
     */
    private static Address[] extractCallerAddressesFromReport(Address[] report) {
        return extractEveryThirdAddressWithOffset(report, 1);
    }

    /**
     * ASSUMPTION: report is an array with the following recurring pattern of addresses:
     *
     *   [ origin, caller, address ]
     *
     * AND: report.length > 0
     */
    private static Address[] extractContractAddressesFromReport(Address[] report) {
        return extractEveryThirdAddressWithOffset(report, 2);
    }

    private static Address[] extractEveryThirdAddressWithOffset(Address[] addresses, int offset) {
        int length = addresses.length;

        // Verify the length is a multiple of 3.
        assertEquals(0, length % 3);

        // The offset can only be in the range [0, 3).
        assertTrue(offset >= 0);
        assertTrue(offset < 3);

        Address[] origins = new Address[length / 3];
        for (int i = 0; i < (length / 3); i++) {
            origins[i] = addresses[(i * 3) + offset];
        }

        return origins;
    }

    private static void printActualOrderOfContractAddresses(Address[] contracts) {
        printAddressesWithLeadingMessage("Actual order of contract addresses", contracts, ",", true);
    }

    private static void printExpectedOrderOfContractAddresses(Address[] contracts) {
        printLineSeparator();
        printAddressesWithLeadingMessage("Expected order of contract addresses", contracts, ",", true);
    }

    private static void printActualOrderOfCallerAddresses(Address[] callers) {
        printAddressesWithLeadingMessage("Actual order of caller addresses", callers, ",", true);
    }

    private static void printExpectedOrderOfCallerAddresses(Address[] callers) {
        printLineSeparator();
        printAddressesWithLeadingMessage("Expected order of caller addresses", callers, ",", true);
    }

    private static void printActualOrderOfOriginAddresses(Address[] origins) {
        printAddressesWithLeadingMessage("Actual order of origin addresses", origins, ",", true);
    }

    private static void printExpectedOrderOfOriginAddresses(int numInternalCalls) {
        Address[] origins = new Address[numInternalCalls + 1];
        for (int i = 0; i < numInternalCalls + 1; i++) {
            origins[i] = from;
        }

        printLineSeparator();
        printAddressesWithLeadingMessage("Expected order of origin addresses", origins, ",", true);
    }

    private static void printOrderOfContractCalls(Address topContract, Address[] otherContracts) {
        printAddressesWithLeadingMessage(
            "Order of contract calls beginning with deployer address",
            joinArrays(new Address[]{ from, topContract }, otherContracts),
            "->",
            false);
    }

    private static void printAddressesWithLeadingMessage(String message, Address[] addresses, String separator, boolean printAsSingleLine) {
        if (ACTIVATE_OUTPUTS_FOR_DEBUGGING) {
            String newlineOrNothing = (printAsSingleLine) ? "" : "\n\t";

            System.out.print(padEndingTo37Characters(message + ":") + "\t");
            System.out.print(newlineOrNothing + addressToStringShortForm(addresses[0]));
            for (Address address : Arrays.copyOfRange(addresses, 1, addresses.length)) {
                System.out.print(" " + separator + " " + addressToStringShortForm(address));
            }
            System.out.println();
        }
    }

    private static void printLineSeparator() {
        if (ACTIVATE_OUTPUTS_FOR_DEBUGGING) {
            System.out.println("-----------------------------------");
        }
    }

    private static void printTestContext(String testName, int numInternalTransactions, boolean recurseFirst) {
        if (ACTIVATE_OUTPUTS_FOR_DEBUGGING) {
            System.out.println("Running " + testName
                + " on " + numInternalTransactions + " internal calls "
                + "with recursion first: " + recurseFirst);
        }
    }

    /**
     * For readability, 4 bytes should be enough to distinguish the addresses..
     */
    private static String addressToStringShortForm(Address address) {
        String addressAsString = address.toString();
        return addressAsString.substring(0, 8) + "..";
    }

    /**
     * Why 37? That's our longest message... this just ensures the tabs all line up; pain to read
     * the logs otherwise.
     */
    private static String padEndingTo37Characters(String string) {
        if (string.length() >= 37) {
            return string;
        } else {
            return string + String.valueOf(new char[37 - string.length()]).replace('\0', ' ');
        }
    }

}
