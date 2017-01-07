package urlshortener.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.domain.ShortUrlStats;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.repository.ShortUrlStatsRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.*;

@Component
public class CheckUrls implements InitializingBean{

    @Autowired private ShortURLRepository shortURLRepository;
    @Autowired private ShortUrlStatsRepository shortURLStatsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CheckUrls.class);
    private LinkedBlockingQueue<ShortURL> queue;
    private final int rate = 3000;

    public CheckUrls(){}

    public void agnadirUrl(ShortURL u){
        try {
            queue.put(u);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        queue = new LinkedBlockingQueue<>();
    }

    @Scheduled(fixedRate = rate)
    public void scheduledCheck(){
        try{

            LinkedBlockingDeque<ShortURL> afterUpdate = new LinkedBlockingDeque<>();

            while(queue.size() > 0){
                ShortURL su = queue.take();
                ShortUrlStats sus = shortURLStatsRepository.findByHash(su.getHash());

                URL urlServidor;
                try{
                    urlServidor = new URL(su.getTarget());

                    long tBefore = System.currentTimeMillis();

                    //Connection
                    HttpURLConnection urlConnection = (HttpURLConnection) urlServidor.openConnection();
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.connect();

                    long tAfter = System.currentTimeMillis();
                    long milliSeconds = tAfter - tBefore;

                    if(urlConnection.getResponseCode() == 200){
                        su.setActive(true);
                        su.setLast_time_up(new Timestamp(Calendar.getInstance().getTime().getTime()));

                        //Check response time and other statistics
                        logger.debug(su.getTarget() + " takes " + milliSeconds + " milliseconds to respond");
                        if(sus != null){
                            //Stats already exist, updating
                            int previousAverage = sus.getRtime_average();
                            int previousNumber = sus.getRtime_number();

                            ShortUrlStats susToUpdateNew = new ShortUrlStats(sus.getHash(),(previousAverage+ (int)milliSeconds),
                                    previousNumber+1, (int) milliSeconds,sus.getD_time(),
                                    sus.getStime_average(),sus.getStime_number());

                            shortURLStatsRepository.update(susToUpdateNew);

                            logger.info("Hash " + susToUpdateNew.getHash() + " stats: " +
                                    "Average response time: " + susToUpdateNew.calculateAverageResponseTime() + ", " +
                                    "Last response time: " + susToUpdateNew.getLast_rtime() + "ms, " +
                                    "Down time: " + susToUpdateNew.getD_time() + "ms, " +
                                    "Average service time: " + susToUpdateNew.calculateAverageServiceTime());
                        }
                    }else{
                        su.setActive(false);
                    }
                } catch(IOException e){
                    su.setActive(false);
                    if(sus != null){
                        ShortUrlStats susToUpdateNew = new ShortUrlStats(sus.getHash(),sus.getRtime_average(),
                                sus.getRtime_number(), sus.getLast_rtime(),sus.getD_time() + rate,
                                sus.getStime_average(),sus.getStime_number());

                        shortURLStatsRepository.update(susToUpdateNew);

                        logger.info("Hash " + susToUpdateNew.getHash() + " isn't reachable, stats: " +
                                "Average response time: " + susToUpdateNew.calculateAverageResponseTime() + ", " +
                                "Last response time: " + susToUpdateNew.getLast_rtime() + "ms, " +
                                "Down time: " + susToUpdateNew.getD_time() + "ms, " +
                                "Average service time: " + susToUpdateNew.calculateAverageServiceTime());
                    }
                }
                su.setLastChange(new Timestamp(Calendar.getInstance().getTime().getTime()));
                shortURLRepository.update(su);
                afterUpdate.put(su);
            }

            while(afterUpdate.size() > 0){
                queue.put(afterUpdate.take());
            }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
