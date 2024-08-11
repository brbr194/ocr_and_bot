package com.example.controller;

import com.example.entity.VehicleLog;
import com.example.mapper.VehicleLogRepository;
import com.example.service.LicensePlateRecognitionService;
import com.openalpr.jni.AlprException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private LicensePlateRecognitionService recognitionService;

    @Autowired
    private VehicleLogRepository vehicleLogRepository;

    @PostMapping("/entry")
    public String vehicleEntry(@RequestParam("file") MultipartFile file) throws IOException, AlprException {
        // 保存上传的图像文件
        //File imageFile = new File("./images/" + file.getOriginalFilename());
        // 获取项目路径
        String projectPath = System.getProperty("user.dir");

        // 构建目标文件夹路径
        String uploadFolder = projectPath + File.separator + "images";

        // 确保目标文件夹存在，如果不存在则创建
        File directory = new File(uploadFolder);
        if (!directory.exists()) {
            directory.mkdirs();  // 创建多级目录
        }

        // 构建保存文件的完整路径
        String fileName = file.getOriginalFilename();
        File imageFile = new File(uploadFolder + File.separator + fileName);
        file.transferTo(imageFile);

        // 调用车牌识别服务
        String licensePlate = recognitionService.recognizeLicensePlate(imageFile.getAbsolutePath());

        if (licensePlate == null) {
            return "车牌识别失败";
        }

        // 记录进场日志
        VehicleLog vehicleLog = new VehicleLog();
        vehicleLog.setLicensePlate(licensePlate);
        vehicleLog.setEntryTime(LocalDateTime.now());
        vehicleLogRepository.save(vehicleLog);

        return "车辆 " + licensePlate + " 进场记录成功";
    }

    @PostMapping("/exit")
    public String vehicleExit(@RequestParam("file") MultipartFile file) throws IOException, AlprException {
        // 保存上传的图像文件
        //File imageFile = new File("/path/to/uploaded_files/" + file.getOriginalFilename());
        // 获取项目路径
        String projectPath = System.getProperty("user.dir");

        // 构建目标文件夹路径
        String uploadFolder = projectPath + File.separator + "images";

        // 确保目标文件夹存在，如果不存在则创建
        File directory = new File(uploadFolder);
        if (!directory.exists()) {
            directory.mkdirs();  // 创建多级目录
        }
        // 构建保存文件的完整路径
        String fileName = file.getOriginalFilename();
        File imageFile = new File(uploadFolder + File.separator + fileName);
        file.transferTo(imageFile);

        // 调用车牌识别服务
        String licensePlate = recognitionService.recognizeLicensePlate(imageFile.getAbsolutePath());

        if (licensePlate == null) {
            return "车牌识别失败";
        }
        // 查找对应车辆的进场记录
        VehicleLog vehicleLog = vehicleLogRepository.findByLicensePlateAndExitTimeIsNull(licensePlate);
        if (vehicleLog == null) {
            return "未找到对应的进场记录";
        }
        // 记录出场时间
        vehicleLog.setExitTime(LocalDateTime.now());
        vehicleLogRepository.save(vehicleLog);

        return "车辆 " + licensePlate + " 出场记录成功";
    }

}