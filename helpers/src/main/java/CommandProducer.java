
//Common interface all classes in jar should implement for correct work
public interface CommandProducer {

    String getInvokationCommand();
    String performCalculation(String[] params);
    int amountParams();
}
