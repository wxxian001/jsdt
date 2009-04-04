/*
 * 这个是一个类
 */

function xx(num, str) {// 说明
	var a = num;
	this.aa = a;
	this.bb = function() {
		alert(str);
	}
	this.cc = function() {
		for (var i = 0; i < 10; i++) {
			document.title = i;
		}
	}
};

xx.prototype.dd = function() {
	alert("d  d        kd");
}

var a = new xx(100, "hello"), b = new xx(0, "ttyp");
if (1 > 2) {
	alert();
} else {
	alert(" hell ");
}

a.bb();
b.dd();
alert(a.aa);

var a = {
	"a" : 'a',
	b : 'c'
}
