package poseteam.pose.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poseteam.pose.common.*;
import poseteam.pose.domain.HashTag;
import poseteam.pose.domain.Img;
import poseteam.pose.service.PoseService;

import java.util.List;

import java.io.*;
import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
@CrossOrigin
public class PoseController {

    private final PoseService poseService;

    //로딩 메인 화면으로, 해시태그 추천기능 / 사진 분류 기능 / 인스타그램 모바일 버전 창 띄우기 기능을 포함하고 있다.
    @RequestMapping(value="/loading", method=RequestMethod.POST)
    public String Loading(@RequestParam("id") String userID, @RequestParam("password") String userPW, Model model){
        model.addAttribute("userID", userID);
        model.addAttribute("userPW", userPW);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);
        return "Background/loading";
    }

    //인스타그램 모바일 버전 창 띄우기 기능(로딩 메인 화면에서 버튼을 클릭하여 동작)
    @RequestMapping(value="/instaStart", method=RequestMethod.GET)
    public String InstaStart(){
        //다중 사용자를 위하여 크롬드라이버를 멀티쓰레드로 동작 시킨다.
        ChromeDriver_MultiThread cm  = new ChromeDriver_MultiThread();
        cm.run();
        return "Background/instaStart";
    }


    //-------------------------------------------------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------------------------//


    //해시태그 추천 기능 (로딩 메인 화면에서 버튼을 클릭하여 동작)
    @RequestMapping(value="/detailsPage", method=RequestMethod.POST)
    public String DetailsPage(@RequestParam("id") String userID, Model model){
        model.addAttribute("userID", userID);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);
        return "Background/detail";
    }

    // 해시태그 추천 기능으로 원하는 사진을 서버로 업로드하여 해당 사진을 유저 이름으로 서버 디렉토리에 저장한다.
    @RequestMapping(value="/load", method=RequestMethod.POST)                                                            //IOException - 파일이 없을 때 발생할 에러. 호출함수인 xml의 DispatcherServlet class로 예외처리 전가
    public String uploadSingle(@RequestParam("id") String userID, @RequestParam("files") MultipartFile report, Model model) throws IOException {    //command객체가 아닌 request로 submit한 값 받아오기     //studentNumber - submissionForm의 속성 name
        //파일명
        String originalFile = report.getOriginalFilename();
        //파일명 중 확장자만 추출                                                //lastIndexOf(".") - 뒤에 있는 . 의 index번호
        String originalFileExtension = originalFile.substring(originalFile.lastIndexOf("."));
        //fileuploadtest.doc
        //lastIndexOf(".") = 14(index는 0번부터)
        //substring(14) = .doc

        //업무에서 사용하는 리눅스, UNIX는 한글지원이 안 되는 운영체제
        //파일업로드시 파일명은 ASCII코드로 저장되므로, 한글명으로 저장 필요
        //UUID클래스 - (특수문자를 포함한)문자를 랜덤으로 생성                    "-"라면 생략으로 대체

        System.out.println(userID);
        //String storedFileName = UUID.randomUUID().toString().replaceAll("-", "") + originalFileExtension;
        String storedFileName = userID + originalFileExtension;


        //파일을 저장하기 위한 파일 객체 생성
        String basePath = "C:\\Users\\pc\\Desktop\\캡스톤\\pose\\pose\\src\\main\\resources\\static\\images\\downloadImg";
        //String filePath = basePath + "/" + file.getOriginalFilename();
        String filePath = basePath + "/";
        File file = new File(filePath + storedFileName);
        //파일 저장
        report.transferTo(file);

        System.out.println("가 업로드한 파일은");
        System.out.println(originalFile + "은 업로드한 파일이다.");
        System.out.println(storedFileName + "라는 이름으로 업로드 됐다.");
        System.out.println("파일사이즈는 " + report.getSize());

        model.addAttribute("userID", userID);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);
        return "Background/detailPageLoading";
    }

    //해시태그 추천 기능으로, 업로드 된 사진을 분석하여 해시태그를 추천하기 위한 ImgRecommendationHashTag 메소드를 동작시킨다.
    @RequestMapping(value="/imageTagRecommend", method=RequestMethod.POST)
    public String TagClass(@RequestParam("id") String userID, Model model){

        ArrayList<HashTag> result = poseService.ImgRecommendationHashTag(userID);
        System.out.println(result);

        String title = result.get(0).getTitle();

        model.addAttribute("title", title);
        model.addAttribute("userID", userID);
        model.addAttribute("result", result);
        return "Background/showRecommendTag"; //여기서 tag 띄워져야함.
    }


    //-------------------------------------------------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------------------------//


    // 사진 분류 기능으로, 실행 로딩 페이지를 띄워줌.
    @RequestMapping(value="/dateImgCrawlingLoading", method=RequestMethod.POST)
    public String dateImgCrawlingLoading(@RequestParam("id") String userID, @RequestParam("pw") String userPW, Model model){
        model.addAttribute("userID", userID);
        model.addAttribute("userPW", userPW);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);
        return "Background/dateImgCrawlingloading";
    }

    //사진 분류 기능으로, 해당 유저의 인스타 계정에 접근해 사진과 게시글 정보를 크롤링 해온다. / DATE별, TAG별 사진 분류 기능
    @RequestMapping(value="/imageSortDate", method=RequestMethod.POST)
    public String ImageSortDate(@RequestParam("id") String userID, @RequestParam("pw") String userPW,Model model){

        //파이썬 코드 삽입 (Date), 인스타 계정에 접근하여 데이터를 크롤링해옴.
        //사진을 서버의 디렉토리에 저장, 게시글 정보는 csv파일로 저장
        poseService.pyImgClassification(userID,userPW);

        //csv파일 읽기, 유저의 게시글 정보를 읽어옴 -> DATE별 사진 분류를 위해 사용됨.
        CSVRead csvRead = new CSVRead();
        List<Img> imgSrcDate = csvRead.readCSV(userID);

        System.out.println("이미지 src data \n");
        System.out.println(imgSrcDate);

        //TAG별 분류를 위해 csv를 읽어 리스트를 만듬.
        System.out.println("태그 분류");
        HashTagClass hashTagClass = new HashTagClass();
        List<String> tag = hashTagClass.Tag(userID);

        model.addAttribute("hashTagClass", tag);
        model.addAttribute("imgSrcDate", imgSrcDate);
        model.addAttribute("userID", userID);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);

        return "Background/index_main";
    }

    //사진 분류 기능으로, Date별 사진 분류에서 년도를 파악하여 데이터를 보여줌.
    @RequestMapping(value="/gallery", method=RequestMethod.GET)
    public String Gallery(@RequestParam("id") String userID, @RequestParam("year") String year, Model model){
        //csv파일 읽기
        CSVRead csvRead = new CSVRead();
        List<Img> imgSrcDate = csvRead.readCSV(userID);

        model.addAttribute("imgSrcDate", imgSrcDate);
        model.addAttribute("year", year);
        model.addAttribute("userID", userID);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);

        return "Background/index_gallery";
    }

    //사진 분류 기능으로, Date별 사진 분류에서 년도와 월을 파악하여 사진을 보여줌.
    @RequestMapping(value="/showPhoto", method=RequestMethod.GET)
    public String ShowPhoto(@RequestParam("id") String userID, @RequestParam("year") String year, @RequestParam("month") String month,Model model){
        CSVRead csvRead = new CSVRead();
        List<Img> imgSrcDate = csvRead.readCSV(userID);

        model.addAttribute("imgSrcDate", imgSrcDate);
        model.addAttribute("year", year);
        model.addAttribute("userID", userID);
        model.addAttribute("month", month);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);
        //System.out.println(year + month + userID);

        return "Background/gallery";
    }

    //사진 분류 기능으로, Tag별로 사진을 보여줌.
    @RequestMapping(value="/TagClass", method=RequestMethod.GET)
    public String TagClass(@RequestParam("id") String userID, @RequestParam("tagName") String tagName, Model model){
        //csv파일 읽기
        CSVRead csvRead = new CSVRead();
        List<Img> imgSrcDate = csvRead.readCSV(userID);

        HashTagClass hashTagClass = new HashTagClass();
        List<String> tag = hashTagClass.Tag(userID);

        model.addAttribute("hashTagClass", tag);
        model.addAttribute("imgSrcDate", imgSrcDate);
        model.addAttribute("userID", userID);
        model.addAttribute("tagName", tagName);
        model.addAttribute("ServerAdd", ServerAdd.serverAdd);

        return "Background/TagGallery";
    }
}
