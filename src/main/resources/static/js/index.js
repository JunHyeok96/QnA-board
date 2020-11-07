var main = {
  init: function () {
    var _this = this;
    $('#btn-save').on('click', function () {
      _this.save();
    });

    $('#btn-update').on('click', function () {
      _this.update();
    });

    $('#btn-delete').on('click', function () {
      _this.delete();
    });
    $('#btn-update-user').on('click', function (){
      _this.userUpdate();
    })
  },
  save: function () {
    var data = {
      title: $('#title').val(),
      userId: $('#userId').val(),
      content: $('#content').val(),
      postType: $('#postType').val(),
      postId: $('#postId').val()
    };

    $.ajax({
      type: 'POST',
      url: '/post/',
      dataType: 'text',
      contentType: 'application/json; charset=utf-8',
      data: JSON.stringify(data)
    }).done(function () {
      alert('글이 등록되었습니다.');
      if (data.postId == "") {
        window.location.href = '/';
      } else {
        window.location.href = '/post/read/' + data.postId;
      }
    }).fail(function (error) {
      alert(JSON.stringify(error));
    });
  },
  update: function () {
    var data = {
      id: $('#id').val(),
      title: $('#title').val(),
      content: $('#content').val()
    };
    $.ajax({
      type: 'PUT',
      url: '/post/' + data.id,
      dataType: 'text',
      contentType: 'application/json; charset=utf-8',
      data: JSON.stringify(data)
    }).done(function () {
      alert('글이 수정되었습니다.');
      window.location.href = '/post/read/' + data.id;
    }).fail(function (error) {
      alert(JSON.stringify(error));
    });
  },
  userUpdate: function () {
    var data = {
      id: $('#id').val(),
      password: $('#password').val(),
      name: $('#name').val(),
      email: $('#email').val()
    };
    $.ajax({
      type: 'PUT',
      url: '/user/' + data.id + "/update",
      dataType: 'text',
      contentType: 'application/json; charset=utf-8',
      data: JSON.stringify(data)
    }).done(function () {
      alert('정보가 수정되었습니다.');
      window.location.href = '/';
    }).fail(function (error) {
      alert(JSON.stringify(error));
    });
  },
  delete: function () {
    var id = $('#id').val();

    $.ajax({
      type: 'DELETE',
      url: '/post/' + id,
      dataType: 'text',
      contentType: 'application/json; charset=utf-8'
    }).done(function () {
      alert('글이 삭제되었습니다.');
      window.location.href = '/';
    }).fail(function (error) {
      alert(JSON.stringify(error));
    });
  }

};

main.init();