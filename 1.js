const a = ['StudentId', 'Name', 'PhoneNumber', 'Email', 'Password']

const str = a
  .map((item) => {
    return `:class="{ 'label-up': has${item}Focused }"`
  })
  .join('     ')
console.log(str)
