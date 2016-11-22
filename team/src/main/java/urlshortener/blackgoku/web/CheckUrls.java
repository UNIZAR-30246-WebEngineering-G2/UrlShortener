package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by AsierHandball on 16/11/2016.
 */
@Component
public class CheckUrls implements  InitializingBean{

    @Autowired
    private ShortURLRepository shortURLRepository;
    private final int NUM_THREADS = 2;                  // Number of threads checking urls
    private final int TIME_DIFF = 5*60*1000;            // Min time difference between two active checks (ms)

    private BlockingDeque<ShortURL> queue;
    //public BlockingQueue<ShortURL> queue;

    private static final Logger logger = LoggerFactory.getLogger(CheckUrls.class);
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Initializing queue");
        queue = new LinkedBlockingDeque<ShortURL>();
        logger.info(shortURLRepository.toString()+"  "+queue);
        Thread[] t = new Thread[NUM_THREADS];
        for (int i=0; i<t.length; i++) {
            t[i] = new Thread(new CheckerThread(i, shortURLRepository, queue));
            t[i].start();
        }
    }
    @Scheduled(fixedRate = 10000)  //Esto hace que se ejecute cada 10k ms
    public void estampillar(){
        Timestamp minTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime() - TIME_DIFF);
        List<ShortURL> list = shortURLRepository.listToUpdate(minTimestamp);
        if(list!=null) {
            for (ShortURL su : list) {
                try {
                    su.setUpdate_status(1);      // Updating active URL
                    //logger.info("Puting " + s.getTarget() + "   active:" + s.getActive() +
                    //" size:" + queue.size() + " status:" + s.getUpdate_status());
                    shortURLRepository.update(su);
                    queue.put(su);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void agnadirUrl(ShortURL u){
        try {
            queue.putLast(u);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
