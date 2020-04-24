  		
		$(function(){
		
		
			//实例化编辑器
		    var um = UM.getEditor('myEditor',{
		    	initialContent:"请输入聊天信息...",
		    	autoHeightEnabled:false,
		    	toolbar:[
		            'source | undo redo | bold italic underline strikethrough | superscript subscript | forecolor backcolor | removeformat |',
		            'insertorderedlist insertunorderedlist | selectall cleardoc paragraph | fontfamily fontsize' ,
		            '| justifyleft justifycenter justifyright justifyjustify |',
		            'link unlink | emotion image video  | map'
		        ]
		    });
		    
			function ajax(obj) {
				var xhr = null;
				if (window.ActiveXObject) {
					xhr = new ActiveXObject('Microsoft.XMLHTTP')
				} else {
					xhr = new XMLHttpRequest();
				}
		
				//打开与服务器的连接
				if (obj.method) {
					xhr.open(obj.method, obj.url, true);
				} else {
					xhr.open('get', obj.url, true);
				}
				xhr.setRequestHeader("Content-Type",
						"application/x-www-form-urlencoded");
				xhr.setRequestHeader("Authorization",
						"APPCODE 3e9dfb924f464e9593a95f9d2bbf4348")
		
				xhr.onreadystatechange = function() {
		
					if (xhr.readyState == 4) {
						//数据接收完毕
						if (xhr.status == 200) {
							//console.log('请求成功',xhr.responseText)
							if (obj.success) {
								obj.success(xhr.responseText)
							}
		
						} else {
							//console.log(xhr.status,'请求出错')
							if (obj.failure) {
								obj.failure('请求失败')
							}
						}
					}
				}
				if (obj.method == undefined || obj.method.toLowerCase() == 'get') {
					xhr.send(null);
				} else {
					xhr.send(obj.params);
		
				}
			}
			
		    var nickname = "匿名用户"+Math.floor(Math.random()*100+1);
		//	var socket = new WebSocket("ws://${pageContext.request.getServerName()}:${pageContext.request.getServerPort()}${pageContext.request.contextPath}/websocket");
		    //接收服务器的消息
		    //接收到消息的回调方法
		//    socket.onmessage=function(ev){
		//    	var obj = eval(   '('+ev.data+')'   );
		//    	addMessage(obj);
		//    }
		    
		    $("#send").click(function(){
		    	if (!um.hasContents()) {  // 判断消息输入框是否为空
		            // 消息输入框获取焦点
		            um.focus();
		            // 添加抖动效果
		            $('.edui-container').addClass('am-animation-shake');
		            setTimeout("$('.edui-container').removeClass('am-animation-shake')", 1000);
		        } else {
		        	//获取输入框的内容
		        	var txt = um.getContent();
		        	//构建一个标准格式的JSON对象
		        	var obj = JSON.stringify({
			    		nickname:nickname,
			    		content:txt
			    	});
		        	
					ajax({
						url : '/Spring-Neo4j-Movie/rest/appleyk/question/query?question='+ txt,
						success : function(res) {
							console.log(res);
						//	addMessage(obj);
		
						}
					})
		            // 发送消息
		         //   socket.send(obj);
		            // 清空消息输入框
		            um.setContent('');
		            // 消息输入框获取焦点
		            um.focus();
		        }
		    
		    });
		    
		    
		    
		    
		    
		});
		
		//人名nickname，时间date，是否自己isSelf，内容content
		function addMessage(msg){
		
			var box = $("#msgtmp").clone(); 	//复制一份模板，取名为box
			box.show();							//设置box状态为显示
			box.appendTo("#chatContent");		//把box追加到聊天面板中
			box.find('[ff="nickname"]').html("匿名用户"); //在box中设置昵称
			box.find('[ff="msgdate"]').html("11:33"); 		//在box中设置时间
			box.find('[ff="content"]').html(msg); 	//在box中设置内容
			box.addClass(msg.isSelf? 'am-comment-flip':'');	//右侧显示
			box.addClass(msg.isSelf? 'am-comment-warning':'am-comment-success');//颜色
			box.css((msg.isSelf? 'margin-left':'margin-right'),"20%");//外边距
			
			$("#ChatBox div:eq(0)").scrollTop(999999); 	//滚动条移动至最底部
			
		}