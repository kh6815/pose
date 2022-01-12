package poseteam.pose.service;

import org.springframework.stereotype.Service;
import poseteam.pose.common.DataTransToPythonSocket;
import poseteam.pose.domain.HashTag;

import java.io.*;
import java.util.ArrayList;

@Service
public class PoseService {

    //HashTag 객체를 통해 여러 추천태그값들을 리스트로 저장하여 return 하는 메서드이다.
    public ArrayList<HashTag> ImgRecommendationHashTag(String userID){
        ArrayList<HashTag> list = new ArrayList<>();
        Process process = null;
        //String result = null;

        String id = userID;

        StringBuffer buffer;
        //BufferedReader bufferedReader;
        BufferedReader reader = null;

        try {
            buffer = new StringBuffer();

            //cmd창으로 해시태그 추천기능을 실행하는 파이썬코드를 동작시킨다.
            buffer.append("cmd.exe ");
            buffer.append("/c ");
            buffer.append("C:\\Users\\pc\\Desktop\\캡스톤\\python\\venv\\Scripts\\python.exe C:/Users/pc/Desktop/캡스톤/python/predict.py");

            process = Runtime.getRuntime().exec(buffer.toString());

            //소켓 생성
            DataTransToPythonSocket.ConnectSocket();

            //파이썬쪽으로 해당 유저의 아이디값(추천을 원하는 사진 파일이 유저 아이디로 생성되기 때문에)을 넘긴다.
            OutputStream output = DataTransToPythonSocket.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            String temp = "123";
            writer.println(id + " " + temp + " ");

            //InputStreamReader를 통해 파이썬 결과값을 가져온다.
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"));
            String line;


            String title= null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(line.contains("end")){
                    System.out.println("end 들어옴");
                    break;
                }
                //원하는 사진을 분석하여 나온 title값을 저장
                if(line.contains("title : ")){
                    title = line.split("title : ")[1];
                    continue;
                }
                //넘어온 tag값을 HashTag 객체로 만들어 리스트에 저장
                HashTag hashTag = new HashTag();
                hashTag.setTag(line);
                hashTag.setTitle(title);
                list.add(hashTag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //사진 분류 기능을 위한 파이썬 파일 실행
    public void pyImgClassification(String userID, String userPW){
        ArrayList<String> list = new ArrayList<>();
        ProcessBuilder pb = null;
        Process process = null;
        BufferedReader reader = null;

        //해당 유저의 ID, PW를 파이썬 코드쪽으로 전송
        String id = userID;
        String pw = userPW;
        try {
            //다중사용자가 동시에 사용가능하도록 Process를 사용
            pb = new ProcessBuilder("python", "C:\\Users\\pc\\Desktop\\캡스톤\\python\\pyImgClassification.py");
            System.out.println(" 프로세스 시작");
            process = pb.start();

            //소켓 실행
            DataTransToPythonSocket.ConnectSocket();

            //OutputStream을 통해 socket으로 데이터(ID, PW)를 전달함.
            OutputStream output = DataTransToPythonSocket.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(id + " " + pw + " ");
            //DataTransToPythonSocket.socket.close();

            //파이썬코드는 CMD창에서 동작하므로 결과값을 print하게 하였고, print한 값을 BufferedReader를 통해 가져온다.
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            //파이썬 코드의 print한 결과값이 끝날 때 같이 계속 값을 가져온다.
            while ((line = reader.readLine()) != null) {
                list.add(line);
                //결과값이 end가 들어오면 끝냄
                if(line.contains("end")){
                    System.out.println("end 들어옴");
                    break;
                }
            }
            process.waitFor(); //프로세스 끝날 때까지 대기

            //해당 동작을 완료하면 process를 제거해준다, BufferedReader도 닫아준다.
        } catch (IOException | InterruptedException e) {
            process.destroy();
            System.out.println(" exception " + e.getLocalizedMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            process.destroy();
        }
    }
}
