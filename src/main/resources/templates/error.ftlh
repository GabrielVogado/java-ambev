<#-- templates/error.ftlh -->

<#import "/lib/template.ftlh" as template>

<@template.header title="Error">

<div class="container">
    <h1>Error: ${error}</h1>
    <p>${errorMessage}</p>
    <p>An unexpected error occurred. Please try again later.</p>
</div>

<@template.footer>
