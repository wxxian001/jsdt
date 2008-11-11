function A(a,b){
	this.a = a;
	this.b = b;
	this.sayHello = function(arg){
		return this.a + this.b + arg;
	}
}
function ttt(arg0,arg1){
	var abc = new A("aaa","bbb");
	alert(abc.sayHello(abc));
}