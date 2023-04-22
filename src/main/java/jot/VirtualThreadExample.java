package jot;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

public class VirtualThreadExample {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		// this starts one virtual and one platform thread
		startThreads();
		
		// this starts a million virtual threads
		startAlotOfVirtualThreads();
	}

	public static void startThreads() throws InterruptedException {

		Runnable run = () -> {
			System.out.println(Thread.currentThread().toString());
		};

		Thread virtualThread = Thread.ofVirtual().start(run);

		virtualThread.join();

		System.out.println("----------------");

		Thread platformThread = Thread.ofPlatform().start(run);

		platformThread.join();

		System.out.println("----------------");
	}

	public static void startAlotOfVirtualThreads() throws IOException {
		
		Instant begin = Instant.now();
		
		Runnable runner = () -> {
			try {
				Thread.sleep(10);
				System.out.println(".");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}	
		};
		
		List<Thread> threads = IntStream.range(0, 1_000_000)
				.mapToObj( i -> Thread.ofVirtual().unstarted(runner)).toList();
		
		threads.forEach(thread -> thread.start());
		
		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		
		// this code is reached when all virtuel threads are finished
		Instant end = Instant.now();
		
		System.out.println(Duration.between(begin, end).toMillis() + "ms");			
	}
}
