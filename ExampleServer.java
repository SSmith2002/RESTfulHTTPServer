public class ExampleServer {
    public static void main(String[] args){
        new ExampleServer();
    }

    public ExampleServer(){
        MyHTTPServer httpServer = new MyHTTPServer(4321);
        httpServer.startServer(this);
    }

    public String test(String test){
        return test;
    }

    public int intTest(int int1,int int2){
        int answer = int1-int2;
        return answer;
    }

}
