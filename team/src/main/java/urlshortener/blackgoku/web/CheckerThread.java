package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by AsierHandball on 17/11/2016.
 */
public class CheckerThread implements Runnable{

    private ShortURLRepository shortURLRepository;
    //private BlockingQueue<ShortURL> queue;
    private BlockingDeque<ShortURL> queue;

    private int id;

    private static final Logger logger = LoggerFactory.getLogger(CheckerThread.class);


    public CheckerThread(int id, ShortURLRepository shortURLRepository, BlockingDeque<ShortURL> queue){
        logger.info(shortURLRepository.toString()+"  "+queue.toString());
        this.shortURLRepository = shortURLRepository;
        this.queue = queue;
        this.id = id;
    }
    @Override
    public void run(){
        while (true){
            try{

                ShortURL su = queue.takeFirst();
                URL urlServidor = null;
                try{
                    urlServidor = new URL(su.getTarget());
                    HttpURLConnection urlConnection = (HttpURLConnection) urlServidor.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.connect();
                    if(urlConnection.getResponseCode() == 200) su.setActive(true);
                    else su.setActive(false);
                } catch(IOException e){
                    su.setActive(false);
                }
                su.setLastChange(new Timestamp(Calendar.getInstance().getTime().getTime()));
                su.setUpdate_status(0);
                shortURLRepository.update(su);
                queue.putLast(su);
                Thread.sleep(100);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}
