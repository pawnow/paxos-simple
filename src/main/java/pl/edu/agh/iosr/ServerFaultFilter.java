package pl.edu.agh.iosr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.DelegatingFilterProxy;
import pl.edu.agh.iosr.service.FaultService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by domin on 05.11.16.
 */
public class ServerFaultFilter extends DelegatingFilterProxy {

    @Autowired
    private FaultService faultService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String url = "";
        if (servletRequest instanceof HttpServletRequest) {
            url = ((HttpServletRequest)servletRequest).getRequestURL().toString();
        }

        if(faultService.isDown() && !url.contains("util")){
            throw new RuntimeException("Node is down");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

}
