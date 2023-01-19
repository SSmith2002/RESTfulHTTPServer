import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RequestHandler implements Runnable{
    private Object serverObject;
    private OutputStream output;
    private ArrayList<String> lines;

    public RequestHandler(Object serverObject,OutputStream output,ArrayList<String> lines){
        this.serverObject = serverObject;
        this.output = output;
        this.lines = lines;
    }
    public void run(){
        System.out.println("Thread");
        String request = lines.get(0);
        String[] splitRequest = request.split(" ");
        String requestType = splitRequest[0];
        request = splitRequest[1];
        String[] functionParams = request.split("\\?");
        String functionName = functionParams[0].substring(1);
        if(functionName.equals("favicon.ico")){
            return;
        }
        String params = functionParams[1];

        HashMap<String,String> paramsHashMap = formatParams(params);

        for(String key:paramsHashMap.keySet()){
            System.out.println(key);
            System.out.println(paramsHashMap.get(key));
        }

        Object result = "";
        Method[] methods = serverObject.getClass().getMethods();//check param names match
        Method method = findMethod(functionName, methods);
        if(!doParamsMatch(paramsHashMap.keySet().toArray(new String[0]), method)){
            System.out.println("params dont match");
            return; //params dont match
        }

        System.out.println(paramsHashMap.values());
        result = executeMethod(serverObject,method, paramsHashMap);

        String response = result.toString();
        sendResponse(response);
    }

    public Object executeMethod(Object invokeOn,Method method,HashMap<String,String> params){
        Parameter[] parameters = method.getParameters();
        
        Object[] paramObjects = new Object[parameters.length];
        int i = 0;
        for(Parameter param:parameters){
            Class<?> type = param.getType();
            System.out.println(type.toString());
            for(String key:params.keySet()){
                if(param.getName().equals(key)){
                    String value = params.get(key);
                    paramObjects[i] = parseArg(type,value);
                    System.out.println("found param");
                }
            }
            i++;
        }
        try{
            return method.invoke(invokeOn, paramObjects);
        }
        catch(Exception e){return null;}
    }

    public Object parseArg(Class<?> requiredType,String value){
        if(requiredType.equals(String.class)){
            return value;
        }
        else if(requiredType.equals(int.class)){
            requiredType = Integer.class;
        }
        else if(requiredType.equals(double.class)){
            requiredType = Double.class;
        }
        else if(requiredType.equals(boolean.class)){
            requiredType = Boolean.class;
        }
        else if(requiredType.equals(short.class)){
            requiredType = Short.class;
        }
        else if(requiredType.equals(long.class)){
            requiredType = Long.class;
        }
        else if(requiredType.equals(float.class)){
            requiredType = Float.class;
        }
        else if(requiredType.equals(byte.class)){
            requiredType = Byte.class;
        }

        try{
            Method method = requiredType.getMethod("valueOf", String.class);
            Object result = method.invoke(null,value);
            return result;
        }
        catch(Exception e){e.printStackTrace();return null;}
    }

    public Method findMethod(String methodName, Method[] methods){
        for (Method method:methods){
            if(method.getName().equals(methodName)){
                System.out.println("found method");
                return method;
            }
        }
        return null;
    }

    public boolean doParamsMatch(String[] paramNames,Method method){
        boolean found;
        Parameter[] parameters = method.getParameters();
        for(Parameter param:parameters){
            found = false;
            for(String name:paramNames){
                System.out.println(param.getName());
                System.out.println(name);
                if(param.getName().equals(name)){
                    found = true;
                }
            }
            if(!found){return false;}
        }
        return true;
    }

    public HashMap<String,String> formatParams(String params){
        HashMap<String,String> paramsHashMap = new HashMap<>();

        String[] paramList = params.split("&");
        for (String param:paramList){
            String[] paramAndValue = param.split("=");
            paramsHashMap.put(paramAndValue[0],paramAndValue[1]);
        }
        
        return paramsHashMap;
    }

    public boolean sendResponse(String message){
        try{
            output.write("HTTP/1.1 200 OK\r\n".getBytes());
            output.write("\r\n".getBytes());
            output.write(("<h1> " + message + " </h1>").getBytes());
            output.write("\r\n\r\n".getBytes());
            output.flush(); 
            output.close();
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

//format sending http response