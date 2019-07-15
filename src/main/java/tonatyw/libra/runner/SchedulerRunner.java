package tonatyw.libra.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class SchedulerRunner implements ApplicationRunner{
	@Override
	public void run(ApplicationArguments args) throws Exception{
//		System.out.println("!!!!!!!!!!!!!!!!!");
//		Scheduler scheduler = new Scheduler();
//		scheduler.init();
//		
//		scheduler.start();
		System.out.println("这个是测试ApplicationRunner接口");
	}
}
