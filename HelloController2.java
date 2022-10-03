import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
@RestController
public class HelloController2 {
	private static final Long REQ_TIMEOUT = 1000L;
	@RequestMapping("/")
	public DeferredResult<ResponseEntity<String>> index() {
		DeferredResult x = new DeferredResult();
		abc1().whenComplete((success,error)->{
			x.setResult(success);
		});
		return x;
		
	}

	public void sendDeleteForString(String polAssoId,CompletableFuture<ResponseEntity<String>> finalResult) {
		CompletableFuture.runAsync(()-> {
			System.out.println(Thread.currentThread().getName()+" b4 polAssoId=" + polAssoId);
			try {
				Thread.sleep(getRandomNumber(90, 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+ " after polAssoId=" + polAssoId);
			boolean x = finalResult.complete(new ResponseEntity<String>(polAssoId,HttpStatus.OK));
			System.out.println(Thread.currentThread().getName()+" defresult set =" + x);
		}).thenAccept(x->{
			System.out.println(Thread.currentThread().getName()+" future complete "+x);

		});
	}

	public CompletableFuture<ResponseEntity<String>> abc1() {
		Stack<String> existingUePolicyAssociationList = new Stack<>();
		existingUePolicyAssociationList.push("1");
		existingUePolicyAssociationList.push("2");
		existingUePolicyAssociationList.push("3");

		CompletableFuture<ResponseEntity<String>> finalResultNew = new CompletableFuture<>();

		finalResultNew.whenComplete((success,error)->{
			System.out.println(Thread.currentThread().getName()+" on completion called");
		});



		sendDefSequentially(existingUePolicyAssociationList,finalResultNew);
		return finalResultNew;
	}

	public void sendDefSequentially(Stack<String> existingUePolicyAssociationList,CompletableFuture<ResponseEntity<String>> finalResultNew ){

		if(existingUePolicyAssociationList.isEmpty()){
			finalResultNew.complete(new ResponseEntity<String>("complete",HttpStatus.OK));
		}else{
			CompletableFuture<ResponseEntity<String>> finalResult = new CompletableFuture<>();

			finalResult.whenComplete((success,error)->{
				System.out.println(Thread.currentThread().getName()+" on complete called");
				sendDefSequentially(existingUePolicyAssociationList,finalResultNew);
			});

			String uePolicyAssociation = existingUePolicyAssociationList.pop();
			sendDeleteForString(uePolicyAssociation, finalResult);
		}
	}

	public void sendDeleteForString(String polAssoId,DeferredResult<ResponseEntity<String>> finalResult) {
		CompletableFuture.runAsync(()-> {
			System.out.println(Thread.currentThread().getName()+" b4 polAssoId=" + polAssoId);
			try {
				Thread.sleep(getRandomNumber(90, 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+ " after polAssoId=" + polAssoId);
			boolean x = finalResult.setResult(new ResponseEntity<String>(polAssoId,HttpStatus.OK));
			System.out.println(Thread.currentThread().getName()+" defresult set =" + x);
		}).thenAccept(x->{
			System.out.println(Thread.currentThread().getName()+" future complete "+x);
			System.out.println(Thread.currentThread().getName()+" result is  "+finalResult.getResult());
			System.out.println(Thread.currentThread().getName()+" result is  "+finalResult.isSetOrExpired());
		});
	}
	public int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}
}
