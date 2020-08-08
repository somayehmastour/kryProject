const listContainer = document.querySelector('#service-list');
let servicesRequest = new Request('/service');
fetch(servicesRequest)
.then(function(response) { return response.json(); })
.then(function(serviceList) {
	var table = document.createElement("table");
	table.style.border = "1px solid #000";
	
	var tr = table.insertRow(-1);
	 var tabCell = tr.insertCell(-1);
     tabCell.innerHTML ="Service";
     tabCell.style.border = "1px solid #000";
     
     var tabCell = tr.insertCell(-1);
     tabCell.innerHTML ="Status";
     tabCell.style.border = "1px solid #000";
     
     var tabCell = tr.insertCell(-1);
     tabCell.innerHTML ="Date";
     tabCell.style.border = "1px solid #000";
     
     var tabCell = tr.insertCell(-1);
     tabCell.innerHTML ="";
     tabCell.style.border = "1px solid #000";
     
     
	 var tr = table.insertRow(-1);    
  serviceList.forEach(service => {
	  tr = table.insertRow(-1);

          var tabCell = tr.insertCell(-1);
          tabCell.innerHTML =service.url;
          tabCell.style.border = "1px solid #000";
          
          var tabCell = tr.insertCell(-1);
          tabCell.innerHTML =service.status;
          tabCell.style.border = "1px solid #000";
          
          var tabCell = tr.insertCell(-1);
          tabCell.innerHTML =service.date;
          tabCell.style.border = "1px solid #000";
          
          var tabCell = tr.insertCell(-1);
          tabCell.innerHTML ="<button onclick = deleteMe("+service.id+")>Delete</button>";
          tabCell.style.border = "1px solid #000";
      
  });
  listContainer.appendChild(table);
});

const saveButton = document.querySelector('#post-service');
saveButton.onclick = evt => {
    let urlName = document.querySelector('#url-name').value;
    fetch('/service', {
    method: 'post',
    headers: {
    'Accept': 'application/json, text/plain, */*',
    'Content-Type': 'application/json'
    },
  body: JSON.stringify({url:urlName})
}).then(res=> location.reload());
}

function deleteMe(id){
	 fetch('/delete', {
		    method: 'post',
		    headers: {
		    'Accept': 'application/json, text/plain, */*',
		    'Content-Type': 'application/json'
		    },
		  body: JSON.stringify({id:id})
		}).then(res=> location.reload());
}
