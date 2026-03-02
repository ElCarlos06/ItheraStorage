export default function Button({ text, onClick, type = "button" }) {
  return (
    <button
      type={type}
      onClick={onClick}
      className="btn w-100 h-100 py-2 mb-3"
      style={{
        borderRadius: "12px",
        backgroundColor: "#5d6cf0ff",
        color: "#ffffff",
      }}
    >
      {text}
    </button>
  );
}
