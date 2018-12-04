public interface CommandProducer {

    String getInvokationCommand();
    String performCalculation(String[] params);
    int amountParams();
}
