public class Addition implements CommandProducer {


    private final String invocationCommand = "/сложить";
    private final int amountParams = 2;
    @Override
    public String getInvokationCommand() {
        return invocationCommand;
    }

    @Override
    public String performCalculation(String[] params) {
        if (params.length!=amountParams) throw  new IllegalArgumentException("Wrong amount of parametrs");
        return (Integer.parseInt(params[0])+Integer.parseInt(params[1]))+"";
    }

    @Override
    public int amountParams() {
        return amountParams;
    }

    public static void main(String[] args) {

    }
}
