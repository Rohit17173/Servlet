document.addEventListener("DOMContentLoaded", loadItems);
document.getElementById("addItemBtn").addEventListener("click", addItem);

function loadItems() {
    fetch("/crud")
        .then(response => response.json())
        .then(data => {
            const itemList = document.getElementById("itemList");
            itemList.innerHTML = "";
            data.forEach(item => {
                const li = document.createElement("li");
                li.textContent = item.name;
                li.innerHTML += `<button onclick="deleteItem(${item.id})">Delete</button>`;
                itemList.appendChild(li);
            });
        });
}

function addItem() {
    const itemName = document.getElementById("itemName").value;
    fetch("/crud", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `name=${encodeURIComponent(itemName)}`
    })
    .then(response => response.text())
    .then(() => {
        document.getElementById("itemName").value = "";
        loadItems();
    });
}

function deleteItem(id) {
    fetch(`/crud?id=${id}`, { method: "DELETE" })
        .then(response => response.text())
        .then(() => loadItems());
}
