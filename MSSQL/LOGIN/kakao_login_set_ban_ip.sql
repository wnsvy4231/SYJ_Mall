/*
	Author      : Seunghwan Shin
	Create date : 2022-10-16
	Description : ip 벤 시켜주는 로직
	    
	History		: 2021-10-16 Seunghwan Shin	#최초 생성
				  2021-10-22 Seunghwan Shin	#banned time 변경
	
	Real DB		: declare	@result int
				  ,			@date datetime = getdate()
				  exec @result = dbo.kakao_login_set_ban_ip '192.168.0.1', @date
				  select @result

*/
alter proc [dbo].[kakao_login_set_ban_ip]
	@user_ip_address varchar(100)	-- 접속한 ip주소
,	@cur_date datetime				-- 접속시도한 날짜 시간
as
set nocount on
set transaction isolation level read uncommitted
begin

	declare @banned_count int = 0

	select
		@banned_count = banned_count
	from dbo.TBLBANNEDIPLIST with(nolock) 
	where banned_ip_address = @user_ip_address
		
	if (@banned_count <> 0)
	begin
		update dbo.TBLBANNEDIPLIST
		set banned_count = @banned_count + 1
		,	banned_end_date = dateadd(MINUTE,(@banned_count+1)*@banned_count,@cur_date)
		where banned_ip_address = @user_ip_address

		if (@@error <> 0) return -1
		else return 0
	end
	else
	begin
		insert into dbo.TBLBANNEDIPLIST
		(
			banned_ip_address
		,	banned_count
		,	banned_end_date
		) 
		values
		(
			@user_ip_address
		,	@banned_count + 1
		,	dateadd(MINUTE,(@banned_count+1)*@banned_count,@cur_date)
		)

		if (@@error <> 0) return -1
		else return 0

	end

	
	return 0

end

