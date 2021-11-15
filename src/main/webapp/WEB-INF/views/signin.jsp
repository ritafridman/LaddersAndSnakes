<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1" />

<link rel="stylesheet" href="/resources/css/style.css">
<link rel="stylesheet" href="/resources/img">
<script type="text/javascript" src="/resources/js/app.js"></script>

<title>snakes and ladders</title>

</head>
<body style="background-color: black;">
	<img class="title" src="/resources/img/title.jpg"
		alt="Snakes and Ladders">

	<form class="form-container" action="/smartspace/users" method="post">
		<div class="form-title">
			<strong>Sign in</strong>
		</div>
		<div class="field-title-a">
			<strong>Name</strong> <input class="form-field" type="text"
				name="username" /><br />
		</div>
		<div class="field-title-b">
			<strong>Email</strong> <input class="form-field" type="text"
				name="email" /><br />
		</div>
		<div class="field-title-b">
			<strong>Avatar</strong> <input class="form-field" type="text"
				name="avatar" /><br />
		</div>
		<div class="field-title-b">
			<strong>Role</strong> <input class="form-field" type="text"
				name="role" /><br />

		</div>
		<br style="margin-bottom: 3000px;" />

		<div class="submit-container">
			<input class="submit-button" type="submit" value="Home" />
		</div>
	</form>

	<br style="margin-bottom: 3000px;" />


</body>
</html>