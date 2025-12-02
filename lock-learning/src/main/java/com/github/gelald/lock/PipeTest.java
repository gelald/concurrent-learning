package com.github.gelald.lock;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import static com.github.gelald.lock.ConcurrentConstant.BRIEFLY_SLEEP_TIME;

@Slf4j
public class PipeTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader();
        // 连击
        writer.connect(reader);

        new Thread(new ReaderThread(reader)).start();
        Thread.sleep(BRIEFLY_SLEEP_TIME);
        new Thread(new WriterThread(writer)).start();
    }

    static class ReaderThread implements Runnable {
        private final PipedReader reader;

        public ReaderThread(PipedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            log.info("this is reader");
            int receive;
            try {
                while ((receive = reader.read()) != -1) {
                    log.info("{}", (char) receive);
                }
            } catch (IOException e) {
                log.error("exception: ", e);
            }
        }
    }

    static class WriterThread implements Runnable {

        private final PipedWriter writer;

        public WriterThread(PipedWriter writer) {
            this.writer = writer;
        }

        @Override
        public void run() {
            log.info("this is writer");
            try {
                writer.write("test");
            } catch (IOException e) {
                log.error("exception: ", e);
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error("exception: ", e);
                }
            }
        }
    }
}
