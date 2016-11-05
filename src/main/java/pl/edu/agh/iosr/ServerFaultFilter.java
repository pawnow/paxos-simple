package pl.edu.agh.iosr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.DelegatingFilterProxy;
import pl.edu.agh.iosr.service.FaultService;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by domin on 05.11.16.
 */
public class ServerFaultFilter extends DelegatingFilterProxy {

    @Autowired
    private FaultService faultService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if(faultService.isDown()){
            //// TODO: 05.11.16 return 404 albo coś w ten deseń
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

}
