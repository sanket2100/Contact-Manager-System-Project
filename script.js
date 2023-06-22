console.log("this is script file");

const toggleSidebar=()=>{
	
	if($(".sidebar").is(":visible"))
	{
		
		$(".sidebar").css("display","none")
		$(".content").css("margin-left","0%")
	}else{
		
		$(".sidebar").css("display","block")
		$(".content").css("margin-left","11%")	
	}
};



//search contact js
const search=()=>{
let query=$("#search-input").val()

if(query=='')
{
	$(".search-result").hide();
}
else
{
	//search url
	let url=`http://localhost:8080/user/search/${query}`;

	fetch(url).then(response=>{
		return response.json();
	}).then((data)=>{
		//data result
		let text=`<div class='list-group'>`
			data.forEach((contact)=>{
			text+=`<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`		

			});


		text+=`</div>`

		$(".search-result").html(text);
		$(".search-result").show();

	});

	

}
};