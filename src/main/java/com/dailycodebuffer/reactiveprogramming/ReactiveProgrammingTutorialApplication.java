package com.dailycodebuffer.reactiveprogramming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class ReactiveProgrammingTutorialApplication {

	public static void main(String[] args) {
		//cuando se haya agregado la dependencia de reator tools ya se puede usar este 
		//debug agent
		ReactorDebugAgent.init();
		SpringApplication.run(ReactiveProgrammingTutorialApplication.class, args);
	}

}


// tutorial
// https://www.youtube.com/watch?v=O26jhgk682Q