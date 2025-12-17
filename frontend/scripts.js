console.log("PinkBot frontend is alive 💗🤖");

async function talkToPinkBot() {
  const res = await fetch("http://localhost:8080/api/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message: "Hi PinkBot!" })
  });

  const data = await res.json();
  console.log("PinkBot says:", data.reply);
}

talkToPinkBot();
