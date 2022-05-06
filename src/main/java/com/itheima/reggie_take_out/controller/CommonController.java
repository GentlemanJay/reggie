package com.itheima.reggie_take_out.controller;

import cn.hutool.core.util.IdUtil;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件的上传和下载
 * @author xushengjie
 * @create 2022/5/1 10:31 PM
 */
@Slf4j
@RequestMapping("/common")
@RestController
public class CommonController {

	@Value("${reggie.file-path}")
	private String baseDir;


	/**
	 * 文件上传
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	public R<String> uploadFile(MultipartFile file) {

		//file是一个本地临时文件，需要将其转存到服务器中，否则本次请求完成后临时文件会被删除

		//获取原始文件名
		String originalFilename = file.getOriginalFilename();
		if (StringUtils.isBlank(originalFilename)) {
			throw new InternalException(ExceptionEnum.FILENAME_INVALID);
		}

		//获取原始文件名的后缀
		String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

		//重命名文件,使用uuid重命名文件，防止文件重名替换
		String newFileName = IdUtil.randomUUID() + suffix;

		//判断文件目录是否已经存在，若不存在则创建目录
		File dir = new File(baseDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}


		//转存临时文件至指定目录
		try {
			file.transferTo(new File(baseDir + newFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}


		return R.success(newFileName);
	}


	/**
	 * 文件下载
	 * @param name
	 * @param response
	 * @return
	 */
	@GetMapping("/download")
	public void downloadFile(@RequestParam("name") String name, HttpServletResponse response) {

		//使用try-with-resources
		try(BufferedInputStream bis =
					new BufferedInputStream(new FileInputStream(new File(baseDir + name)));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

			response.setContentType("image/jpeg");
			int len = 0;
			byte[] bytes = new byte[1024];
			while ((len = bis.read(bytes)) != -1) {
				bos.write(bytes, 0, len);
				bos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
