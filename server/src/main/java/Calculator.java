public class Calculator implements Runnable {

    private CommandProducer commandProducer;
    private String[] params;
    private String result = "";
    private Connection connection;

    public String getResult(){
        return result;
    }
    public Calculator(CommandProducer commandProducer, String[] params, Connection connection) throws IllegalArgumentException{
        System.out.println("toooot");
        if (params.length!=commandProducer.amountParams())
            throw new IllegalArgumentException("Wrong amount of arguments. Expected: "+commandProducer.amountParams()+
                    ", received: "+params.length);
        this.commandProducer = commandProducer;
        this.params = params;
        this.connection = connection;
    }
    @Override
    public void run() {
        System.out.println("tooooot");
        result = commandProducer.performCalculation(params);
        connection.sendString(result);
    }
}
