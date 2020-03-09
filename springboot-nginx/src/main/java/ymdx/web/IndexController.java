package ymdx.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IndexController
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-07 22:58
 * @Version: 1.0
 **/
@RestController
public class IndexController {

    @Value("${server.port}")
    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @RequestMapping("/")
    public String index(){
        return "SpringBoot:" + port;
    }

}
